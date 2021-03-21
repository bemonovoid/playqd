package com.bemonovoid.playqd.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.bemonovoid.playqd.core.helpers.EntityNameHelper;
import com.bemonovoid.playqd.core.model.DirectoryScanLog;
import com.bemonovoid.playqd.core.model.DirectoryScanStatus;
import com.bemonovoid.playqd.core.model.event.DirectoryScanCompleted;
import com.bemonovoid.playqd.core.service.LibraryDirectory;
import com.bemonovoid.playqd.core.service.MusicLibraryScanner;
import com.bemonovoid.playqd.datasource.jdbc.batch.BatchOperation;
import com.bemonovoid.playqd.datasource.jdbc.batch.BatchTable;
import com.bemonovoid.playqd.datasource.jdbc.batch.InsertBatch;
import com.bemonovoid.playqd.datasource.jdbc.entity.AlbumEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.AlbumPreferencesEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.ArtistEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.SongEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.SongPreferencesEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.system.AuditableEntity;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.generic.Utils;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
class MusicLibraryScannerImpl implements MusicLibraryScanner {

    private static final Logger LOG = LoggerFactory.getLogger(MusicLibraryScannerImpl.class);

    private static final String UNKNOWN_ARTIST = "Unknown artist";
    private static final String UNKNOWN_ALBUM = "Unknown album";

    private static final Set<String> AUDIO_EXTENSIONS = Set.of("flac", "m4a", "m4p", "mp3", "ogg", "wav", "wma");

    private static final List<String> TABLES = List.of(
            SongPreferencesEntity.TABLE_NAME,
            SongEntity.TABLE_NAME,
            AlbumPreferencesEntity.TABLE_NAME,
            AlbumEntity.TABLE_NAME,
            ArtistEntity.TABLE_NAME);

    private final InsertBatch songInsertBatch;

    private LibraryData libraryData;

    private final JdbcTemplate jdbcTemplate;
    private final LibraryDirectory libraryDirectory;
    private final ApplicationEventPublisher eventPublisher;

    MusicLibraryScannerImpl(JdbcTemplate jdbcTemplate,
                            LibraryDirectory libraryDirectory,
                            ApplicationEventPublisher eventPublisher) {
        this.jdbcTemplate = jdbcTemplate;
        this.libraryDirectory = libraryDirectory;
        this.eventPublisher = eventPublisher;
        this.songInsertBatch = new InsertBatch(1000, new BatchTable(SongEntity.TABLE_NAME, SongEntity.COL_PK_ID));
    }

    @Override
    @Async
    public void scan(boolean deleteTables) {
        libraryData = new LibraryData();
        if (deleteTables) {
            dropTables();
        } else {
            libraryData = getLibraryData();
        }

        LocalTime scanStartTime = LocalTime.now();
        Set<String> libraryFiles = libraryData.getFileLocations();

        DirectoryScanLog.DirectoryScanLogBuilder dirScanLogBuilder = DirectoryScanLog.builder()
                .cleanAllApplied(deleteTables).directory(libraryDirectory.basePath().toString());

        try (Stream<Path> allPaths = Files.walk(libraryDirectory.basePath(), 20)) {
            List<File> files = allPaths
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .filter(f -> AUDIO_EXTENSIONS.contains(getFileExtension(f)))
                    .filter(f -> !libraryFiles.contains(f.getAbsolutePath()))
                    .collect(Collectors.toList());
            if (!files.isEmpty()) {
                LOG.info("Found {} files. Adding to database ...", files.size());
                files.forEach(this::scanFile);
                int songsAdded = new BatchOperation(jdbcTemplate).insert(songInsertBatch).execute();
                dirScanLogBuilder.numberOfSongsAdded(songsAdded);
            } else {
                LOG.info("No new files found");
            }
            dirScanLogBuilder.status(DirectoryScanStatus.COMPLETED);
        } catch (Exception e) {
            dirScanLogBuilder.status(DirectoryScanStatus.FAILED);
            throw new RuntimeException(e);
        } finally {
            libraryData = null;
            Duration duration = Duration.between(scanStartTime, LocalTime.now());
            DirectoryScanLog directoryScanLog = dirScanLogBuilder.duration(duration).build();
            eventPublisher.publishEvent(new DirectoryScanCompleted(this, directoryScanLog));
            LOG.info("Scan completed");
        }
    }

    private void scanFile(File file) {
        try {
            scanAudioFile(AudioFileIO.read(file));
        } catch (Exception e) {
            LOG.error(String.format("Failed to refresh a file: %s", file.getAbsolutePath()), e);
        }
    }

    private void scanAudioFile(AudioFile audiofile) {

        LibraryArtistItem libraryArtistItem = getArtistId(audiofile);

        String artistId = libraryArtistItem.getId();
        String albumId = getAlbumId(libraryArtistItem, audiofile);

        MapSqlParameterSource params = new MapSqlParameterSource();
        String fileName = getFileNameWithoutExtension(audiofile.getFile());

        params.addValue(SongEntity.COL_PK_ID, UUID.randomUUID());

        if (audiofile.getTag() != null) {
            Tag tag = audiofile.getTag();
            String songName = tag.getFirst(FieldKey.TITLE);
            if (songName == null || songName.isBlank()) {
                songName = fileName;
            }

            params
                    .addValue(SongEntity.COL_NAME, songName)
                    .addValue(SongEntity.COL_TRACK_ID, resolveTrackId(tag.getFirst(FieldKey.TRACK)))
                    .addValue(SongEntity.COL_COMMENT, tag.getFirst(FieldKey.COMMENT))
                    .addValue(SongEntity.COL_LYRICS, tag.getFirst(FieldKey.LYRICS));

        } else {
            params.addValue(SongEntity.COL_NAME, fileName);
        }

        AudioHeader audioHeader = audiofile.getAudioHeader();

        params
                .addValue(SongEntity.COL_ARTIST_ID, artistId)
                .addValue(SongEntity.COL_ALBUM_ID, albumId)
                .addValue(SongEntity.COL_AUDIO_ENCODING_TYPE, audioHeader.getEncodingType())
                .addValue(SongEntity.COL_AUDIO_SAMPLE_RATE, audioHeader.getSampleRate())
                .addValue(SongEntity.COL_AUDIO_BIT_RATE, audioHeader.getBitRate())
                .addValue(SongEntity.COL_AUDIO_CHANNEL_TYPE, audioHeader.getChannels())
                .addValue(SongEntity.COL_DURATION, audioHeader.getTrackLength())
                .addValue(SongEntity.COL_FILE_NAME, fileName)
                .addValue(SongEntity.COL_FILE_LOCATION, audiofile.getFile().getAbsolutePath())
                .addValue(SongEntity.COL_FILE_EXTENSION, audiofile.getExt())
                .addValue(SongEntity.COL_PLAY_COUNT, 0)
                .addValue(SongEntity.COL_FAVORITE, false);

        addAuditableParams(params);

        songInsertBatch.append(params);
    }

    private LibraryArtistItem getArtistId(AudioFile audioFile) {
        String name = getArtistName(audioFile);
        String nameAsKey = EntityNameHelper.toLookUpName(name);
        return libraryData.artists.computeIfAbsent(nameAsKey, value -> {
            SimpleJdbcInsert artistsJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
            artistsJdbcInsert.withTableName(ArtistEntity.TABLE_NAME);
            UUID artistUUID = UUID.randomUUID();
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue(ArtistEntity.COL_PK_ID, artistUUID)
                    .addValue(ArtistEntity.COL_NAME, name)
                    .addValue(ArtistEntity.COL_SIMPLE_NAME, nameAsKey);
            Tag tag = audioFile.getTag();
            if (tag != null) {
                params.addValue(ArtistEntity.COL_COUNTRY, tag.getFirst(FieldKey.COUNTRY));
            }
            addAuditableParams(params);
            artistsJdbcInsert.execute(params);
            return new LibraryArtistItem(artistUUID.toString(), nameAsKey);
        });
    }

    private String getAlbumId(LibraryArtistItem libraryArtistItem, AudioFile audioFile) {
        String name = getAlbumName(audioFile);
        String nameAsKey = EntityNameHelper.toLookUpName(name);

        return libraryArtistItem.albums.computeIfAbsent(nameAsKey, value -> {
            SimpleJdbcInsert albumJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
            albumJdbcInsert.withTableName(AlbumEntity.TABLE_NAME);
            UUID albumUUID = UUID.randomUUID();
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue(AlbumEntity.COL_PK_ID, albumUUID)
                    .addValue(AlbumEntity.COL_NAME, name)
                    .addValue(AlbumEntity.COL_SIMPLE_NAME, nameAsKey)
                    .addValue(AlbumEntity.COL_ARTIST_ID, libraryArtistItem.getId());
            if (audioFile.getTag() != null) {
                Tag tag = audioFile.getTag();
                params
                        .addValue(AlbumEntity.COL_GENRE, tag.getFirst(FieldKey.GENRE))
                        .addValue(AlbumEntity.COL_DATE, tag.getFirst(FieldKey.YEAR));
            }
            addAuditableParams(params);
            albumJdbcInsert.execute(params);
            return new LibraryAlbumItem(albumUUID.toString(), nameAsKey);
        }).getId();
    }

    private static String getArtistName(AudioFile audioFile) {
        String name = null;
        Tag tag = audioFile.getTag();
        if (tag != null) {
            name = tag.getFirst(FieldKey.ARTIST);
            if (name == null || name.isBlank()) {
                name = tag.getFirst(FieldKey.ALBUM_ARTIST);
            }
            if (name == null || name.isBlank()) {
                name = tag.getFirst(FieldKey.ORIGINAL_ARTIST);
            }
            if (name == null || name.isBlank()) {
                name = tag.getFirst(FieldKey.COMPOSER);
            }
        }
        if (name == null || name.isBlank()) {
            name = UNKNOWN_ARTIST;
        }
        return name.trim();
    }

    private static String getAlbumName(AudioFile audioFile) {
        Tag tag = audioFile.getTag();
        if (tag != null) {
            String name = tag.getFirst(FieldKey.ALBUM);
            if (name != null && !name.isBlank()) {
                return name.trim();
            }
        }
        return UNKNOWN_ALBUM;
    }

    private static String getFileNameWithoutExtension(File file) {
        String fileName = file.getName();
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    private static String getFileExtension(File file) {
        return Utils.getExtension(file);
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

    private void dropTables() {
        TABLES.forEach(tableName -> jdbcTemplate.execute(String.format("TRUNCATE TABLE %s", tableName)));
    }

    private static void addAuditableParams(MapSqlParameterSource params) {
        params.addValue(AuditableEntity.COL_CREATED_BY, "system");
        params.addValue(AuditableEntity.COL_CREATED_DATE, LocalDateTime.now());
    }

    private LibraryData getLibraryData() {
        LibraryData libraryData = new LibraryData();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT artist.ID as artistId, artist.SIMPLE_NAME as artistName, album.ID as albumId, album.SIMPLE_NAME as albumName, s.FILE_LOCATION as fileLocation " +
                "FROM SONG s " +
                "JOIN ALBUM album on album.ID = s.ALBUM_ID " +
                "JOIN ARTIST artist on artist.ID = s.ARTIST_ID");
        rows.forEach(row -> {
            LibraryArtistItem artist = new LibraryArtistItem((String) row.get("artistId"), row.get("artistName").toString());
            LibraryAlbumItem album = new LibraryAlbumItem((String) row.get("albumId"), row.get("albumName").toString());
            LibraryArtistItem libraryArtist = libraryData.artists.computeIfAbsent(artist.getName(), value -> artist);
            libraryArtist.albums.computeIfAbsent(album.getName(), value -> album);
            libraryData.fileLocations.add(row.get("fileLocation").toString());
        });
        return libraryData;
    }

    @Getter
    private static class LibraryData {
        private final Map<String, LibraryArtistItem> artists = new HashMap<>();
        private final Set<String> fileLocations = new HashSet<>();
    }

    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode(exclude = "albums")
    private static class LibraryArtistItem {
           private final String id;
           private final String name;
           private final Map<String, LibraryAlbumItem> albums = new HashMap<>();
    }

    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    private static class LibraryAlbumItem {
        private final String id;
        private final String name;
    }
}
