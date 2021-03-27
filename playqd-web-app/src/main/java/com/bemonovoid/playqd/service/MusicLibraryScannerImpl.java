package com.bemonovoid.playqd.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import com.bemonovoid.playqd.core.exception.PlayqdConfigurationException;
import com.bemonovoid.playqd.core.helpers.AudioFileUtils;
import com.bemonovoid.playqd.core.helpers.EntityNameHelper;
import com.bemonovoid.playqd.core.model.DirectoryScanStatus;
import com.bemonovoid.playqd.core.model.ScanOptions;
import com.bemonovoid.playqd.core.model.ScannerLog;
import com.bemonovoid.playqd.core.model.event.ScanCompletedEvent;
import com.bemonovoid.playqd.core.service.LibraryDirectory;
import com.bemonovoid.playqd.core.service.MusicLibraryScanner;
import com.bemonovoid.playqd.core.service.SecurityService;
import com.bemonovoid.playqd.datasource.jdbc.entity.AlbumEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.ArtistEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.SongEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.system.AuditableEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.generic.Utils;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
class MusicLibraryScannerImpl implements MusicLibraryScanner {

    private static final String UNKNOWN_ARTIST = "Unknown artist";
    private static final String UNKNOWN_ALBUM = "Unknown album";
    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("flac", "m4a", "m4p", "mp3", "ogg", "wav", "wma");

    private final JdbcTemplate jdbcTemplate;
    private final LibraryDirectory libraryDirectory;
    private final ApplicationEventPublisher eventPublisher;

    private Map<String, ArtistAlbums> artists;
    private Set<String> indexedFiles;

    MusicLibraryScannerImpl(JdbcTemplate jdbcTemplate,
                            LibraryDirectory libraryDirectory,
                            ApplicationEventPublisher eventPublisher) {
        this.jdbcTemplate = jdbcTemplate;
        this.libraryDirectory = libraryDirectory;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void scan(ScanOptions options) {

        prepare(options);

        LocalTime scanStartedAt = LocalTime.now();
        ScannerLog.ScannerLogBuilder scannerLogBuilder = ScannerLog.builder()
                .deleteAllBeforeScan(options.isDeleteAllBeforeScan())
                .directory(libraryDirectory.basePath().toString());

        try {
            int numberOfFilesIndexed = Files.list(libraryDirectory.basePath()).mapToInt(this::scanDirectory).sum();
            log.info("Scan completed. Total audio files indexed: {}", numberOfFilesIndexed);

            scannerLogBuilder.filesIndexed(numberOfFilesIndexed).status(DirectoryScanStatus.COMPLETED);

        } catch (IOException e) {
            scannerLogBuilder.status(DirectoryScanStatus.FAILED);
            throw new PlayqdConfigurationException(e);
        } finally {
            clean();
            ScannerLog scannerLog = scannerLogBuilder.duration(Duration.between(scanStartedAt, LocalTime.now())).build();
            eventPublisher.publishEvent(new ScanCompletedEvent(this, scannerLog));
        }
    }

    private int scanDirectory(Path path) {
        SqlParameterSource[] sources;
        try (Stream<Path> subdirectories = Files.walk(path, 100).parallel()) {
            sources = subdirectories
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .filter(f -> SUPPORTED_EXTENSIONS.contains(Utils.getExtension(f)))
                    .filter(f -> !indexedFiles.contains(f.getAbsolutePath()))
                    .map(AudioFileUtils::readAudioFile)
                    .filter(Objects::nonNull)
                    .map(this::tagsToInsertQuery)
                    .toArray(SqlParameterSource[]::new);
        } catch (IOException e) {
            throw new PlayqdConfigurationException(e);
        }
        if (sources.length == 0) {
            log.debug("Directory scanned: {}. Audio files indexed: 0. " +
                    "Directory might be empty or all the files were indexed already", path.toString());
            return 0;
        }
        SimpleJdbcInsert songsJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName(SongEntity.TABLE_NAME);
        int[] rows = songsJdbcInsert.executeBatch(sources);
        log.info("Directory scanned: {}. Audio files indexed: {}", path.toString(), rows.length);
        return rows.length;
    }

    private SqlParameterSource tagsToInsertQuery(AudioFile audioFile) {
        ArtistAlbumItem artistAlbumItem = parseArtistFromFile(audioFile);

        MapSqlParameterSource params = fromAuditableParams();
        String fileName = AudioFileUtils.getFileNameWithoutExtension(audioFile);

        params.addValue(SongEntity.COL_PK_ID, UUID.randomUUID());

        Optional.ofNullable(audioFile.getTag()).ifPresentOrElse(
                tag -> {
                    String songName = tag.getFirst(FieldKey.TITLE);
                    if (songName == null || songName.isBlank()) {
                        songName = fileName;
                    }
                    params
                            .addValue(SongEntity.COL_NAME, songName)
                            .addValue(SongEntity.COL_COMMENT, tag.getFirst(FieldKey.COMMENT))
                            .addValue(SongEntity.COL_TRACK_ID, resolveTrackId(tag.getFirst(FieldKey.TRACK)))
                            .addValue(SongEntity.COL_LYRICS, AudioFileUtils.readTagOrNull(audioFile, FieldKey.LYRICS));
                },
                () -> params.addValue(SongEntity.COL_NAME, fileName));

        AudioHeader audioHeader = audioFile.getAudioHeader();

        params
                .addValue(SongEntity.COL_ARTIST_ID, artistAlbumItem.artistId)
                .addValue(SongEntity.COL_ALBUM_ID, artistAlbumItem.albumId)
                .addValue(SongEntity.COL_AUDIO_ENCODING_TYPE, audioHeader.getEncodingType())
                .addValue(SongEntity.COL_AUDIO_SAMPLE_RATE, audioHeader.getSampleRate())
                .addValue(SongEntity.COL_AUDIO_BIT_RATE, audioHeader.getBitRate())
                .addValue(SongEntity.COL_AUDIO_CHANNEL_TYPE, audioHeader.getChannels())
                .addValue(SongEntity.COL_DURATION, audioHeader.getTrackLength())
                .addValue(SongEntity.COL_FILE_NAME, fileName)
                .addValue(SongEntity.COL_FILE_LOCATION, audioFile.getFile().getAbsolutePath())
                .addValue(SongEntity.COL_FILE_EXTENSION, audioFile.getExt())
                .addValue(SongEntity.COL_PLAY_COUNT, 0)
                .addValue(SongEntity.COL_FAVORITE, false);

        return params;
    }

    private ArtistAlbumItem parseArtistFromFile(AudioFile audioFile) {
        String artistName = getArtistName(audioFile);
        String artistKey = EntityNameHelper.toLookUpName(artistName);
        ArtistAlbums artistAlbums = artists.computeIfAbsent(artistKey, value -> {
            SimpleJdbcInsert artistsJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
            artistsJdbcInsert.withTableName(ArtistEntity.TABLE_NAME);
            UUID artistUUID = UUID.randomUUID();

            MapSqlParameterSource params = fromAuditableParams()
                    .addValue(ArtistEntity.COL_PK_ID, artistUUID)
                    .addValue(ArtistEntity.COL_NAME, artistName);

            Optional.ofNullable(audioFile.getTag())
                    .ifPresent(tag -> params.addValue(ArtistEntity.COL_COUNTRY, tag.getFirst(FieldKey.COUNTRY)));

            artistsJdbcInsert.execute(params);

            return new ArtistAlbums(artistUUID.toString());
        });

        String albumName = getAlbumName(audioFile);

        String albumId = artistAlbums.albumNameId.computeIfAbsent(albumName, value -> {

            SimpleJdbcInsert albumJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName(AlbumEntity.TABLE_NAME);

            UUID albumUUID = UUID.randomUUID();

            MapSqlParameterSource params = fromAuditableParams()
                    .addValue(AlbumEntity.COL_PK_ID, albumUUID)
                    .addValue(AlbumEntity.COL_NAME, albumName)
                    .addValue(AlbumEntity.COL_ARTIST_ID, artistAlbums.artistId);

            Optional.ofNullable(audioFile.getTag()).ifPresent(tag ->
                    params
                            .addValue(AlbumEntity.COL_GENRE, tag.getFirst(FieldKey.GENRE))
                            .addValue(AlbumEntity.COL_DATE, tag.getFirst(FieldKey.YEAR)));

            albumJdbcInsert.execute(params);

            return albumUUID.toString();
        });

        return new ArtistAlbumItem(artistAlbums.artistId, albumId);
    }

    private void prepare(ScanOptions options) {
        if (options.isDeleteAllBeforeScan()) {
            List.of(SongEntity.TABLE_NAME, AlbumEntity.TABLE_NAME, ArtistEntity.TABLE_NAME)
                    .forEach(tableName -> jdbcTemplate.execute(String.format("TRUNCATE TABLE %s", tableName)));
            log.info("Preparing to scan. Library tables truncated.");
            artists = Collections.synchronizedMap(new LinkedHashMap<>());
        } else {
            artists = Collections.synchronizedMap(findIndexedFiles());
        }
    }

    private void clean() {
        artists = null;
        indexedFiles = null;
    }

    private Map<String, ArtistAlbums> findIndexedFiles() {
        Map<String, ArtistAlbums> indexedArtists = new HashMap<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT artist.ID as artistId, artist.NAME as artistName, album.ID as albumId, album.NAME as albumName, s.FILE_LOCATION as fileLocation " +
                        "FROM SONG s " +
                        "JOIN ALBUM album on album.ID = s.ALBUM_ID " +
                        "JOIN ARTIST artist on artist.ID = s.ARTIST_ID");
        indexedFiles = new HashSet<>(rows.size());
        rows.forEach(row -> {
            String artistId = row.get("artistId").toString();
            String artistName = EntityNameHelper.toLookUpName(row.get("artistName").toString());
            String albumId = row.get("albumId").toString();
            String albumName = EntityNameHelper.toLookUpName(row.get("albumName").toString());
            ArtistAlbums artistAlbums = indexedArtists.computeIfAbsent(artistName, v -> new ArtistAlbums(artistId));
            artistAlbums.albumNameId.computeIfAbsent(albumName, v -> albumId);
            indexedFiles.add(row.get("fileLocation").toString());
        });
        return indexedArtists;
    }

    private static String getArtistName(AudioFile audioFile) {
        return Optional.ofNullable(audioFile.getTag())
                .map(tag -> {
                    String name;
                    name = tag.getFirst(FieldKey.ARTIST);
                    if (!StringUtils.hasText(name)) {
                        name = tag.getFirst(FieldKey.ALBUM_ARTIST);
                    }
                    if (!StringUtils.hasText(name)) {
                        name = AudioFileUtils.readTagOrNull(audioFile, FieldKey.ORIGINAL_ARTIST);
                    }
                    if (!StringUtils.hasText(name)) {
                        name = tag.getFirst(FieldKey.COMPOSER);
                    }
                    if (!StringUtils.hasText(name)) {
                        name = UNKNOWN_ARTIST;
                    }
                    return name.trim();
                })
                .orElse(UNKNOWN_ARTIST);
    }

    private static String getAlbumName(AudioFile audioFile) {
        Tag tag = audioFile.getTag();
        if (tag != null) {
            String name = tag.getFirst(FieldKey.ALBUM);
            if (StringUtils.hasText(name)) {
                return name.trim();
            }
        }
        return UNKNOWN_ALBUM;
    }

    private static MapSqlParameterSource fromAuditableParams() {
        return new MapSqlParameterSource()
                .addValue(AuditableEntity.COL_CREATED_BY, SecurityService.getCurrentUserName())
                .addValue(AuditableEntity.COL_CREATED_DATE, LocalDateTime.now());
    }

    private static int resolveTrackId(String trackId) {
        if (StringUtils.hasText(trackId)) {
            String resolvedTrackId = trackId;
            if (resolvedTrackId.length() > 1 && resolvedTrackId.startsWith("0")) {
                resolvedTrackId = resolvedTrackId.replaceFirst("0", "");
            }
            return Integer.parseInt(resolvedTrackId);
        }
        return 0;
    }

    private static class ArtistAlbums {
        private final String artistId;

        private final Map<String, String> albumNameId = Collections.synchronizedMap(new LinkedHashMap<>());
        private ArtistAlbums(String artistId) {
            this.artistId = artistId;
        }

    }
    @AllArgsConstructor
    private static class ArtistAlbumItem {
        private final String artistId;
        private final String albumId;

    }

}
