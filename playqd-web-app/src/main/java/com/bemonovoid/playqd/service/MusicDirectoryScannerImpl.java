package com.bemonovoid.playqd.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.bemonovoid.playqd.core.helpers.EntityNameHelper;
import com.bemonovoid.playqd.core.service.LibraryDirectory;
import com.bemonovoid.playqd.core.service.MusicDirectoryScanner;
import com.bemonovoid.playqd.datasource.jdbc.batch.BatchInsert;
import com.bemonovoid.playqd.datasource.jdbc.batch.SimpleBatchInsert;
import com.bemonovoid.playqd.datasource.jdbc.entity.AlbumEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.ArtistEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.FavoriteSongEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.PersistentAuditableEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.PlaybackHistoryEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.SongEntity;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.generic.Utils;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
class MusicDirectoryScannerImpl implements MusicDirectoryScanner {

    private static final Logger LOG = LoggerFactory.getLogger(MusicDirectoryScannerImpl.class);

    private static final String UNKNOWN_ARTIST = "Unknown artist";
    private static final String UNKNOWN_ALBUM = "Unknown album";

    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpeg", "jpg", "bmp", "png");
    private static final Set<String> AUDIO_EXTENSIONS = Set.of("flac", "m4a", "m4p", "mp3", "ogg", "wav", "wma");

    private static final List<String> TABLES = List.of(
            FavoriteSongEntity.TABLE_NAME, PlaybackHistoryEntity.TABLE_NAME, SongEntity.TABLE_NAME, AlbumEntity.TABLE_NAME, ArtistEntity.TABLE_NAME);

    private final BatchInsert songBatch;

    private final Map<String, Long> artists = new HashMap<>();
    private final Map<AlbumArtistKey, Long> artistAlbums = new HashMap<>();

    private final JdbcTemplate jdbcTemplate;
    private final LibraryDirectory libraryDirectory;

    MusicDirectoryScannerImpl(JdbcTemplate jdbcTemplate, LibraryDirectory libraryDirectory) {
        this.jdbcTemplate = jdbcTemplate;
        this.libraryDirectory = libraryDirectory;
        this.songBatch = new SimpleBatchInsert(jdbcTemplate, 1000, SongEntity.TABLE_NAME, SongEntity.COL_PK_ID);
    }

    @Override
    @Async
    public void scan() {

        deleteAllTAbles();

        try (Stream<Path> allPaths = Files.walk(libraryDirectory.basePath(), 20)) {
            allPaths
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .filter(f -> AUDIO_EXTENSIONS.contains(getFileExtension(f)))
                    .forEach(this::scanFile);
            songBatch.insertAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            artists.clear();
            artistAlbums.clear();
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

        long artistId = getArtistId(audiofile);
        long albumId = getAlbumId(artistId, audiofile);

        MapSqlParameterSource params = new MapSqlParameterSource();
        String fileName = getFileNameWithoutExtension(audiofile.getFile());

        if (audiofile.getTag() != null) {
            Tag tag = audiofile.getTag();
            String songName = tag.getFirst(FieldKey.TITLE);
            if (songName == null || songName.isBlank()) {
                songName = fileName;
            }

            params
                    .addValue(SongEntity.COL_NAME, songName)
                    .addValue(SongEntity.COL_TRACK_ID, tag.getFirst(FieldKey.TRACK))
                    .addValue(SongEntity.COL_COMMENT, tag.getFirst(FieldKey.COMMENT))
                    .addValue(SongEntity.COL_LYRICS, tag.getFirst(FieldKey.LYRICS))
                    .addValue(SongEntity.COL_MB_TRACK_ID, tag.getFirst(FieldKey.MUSICBRAINZ_TRACK_ID));

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
                .addValue(SongEntity.COL_FILE_EXTENSION, audiofile.getExt());

        addAuditableParams(params);

        songBatch.insert(params);
    }

    private Long getArtistId(AudioFile audioFile) {
        String name = getArtistName(audioFile);
        String nameAsKey = EntityNameHelper.toLookUpName(name);

        if (artists.containsKey(nameAsKey)) {
            return artists.get(nameAsKey);
        } else {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
            simpleJdbcInsert.withTableName(ArtistEntity.TABLE_NAME).usingGeneratedKeyColumns(ArtistEntity.COL_PK_ID);
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue(ArtistEntity.COL_NAME, name)
                    .addValue(ArtistEntity.COL_SIMPLE_NAME, nameAsKey);
            Tag tag = audioFile.getTag();
            if (tag != null) {
                params.addValue(ArtistEntity.COL_COUNTRY, tag.getFirst(FieldKey.COUNTRY));
                params.addValue(ArtistEntity.COL_MB_ARTIST_ID, tag.getFirst(FieldKey.MUSICBRAINZ_ARTISTID));
            }
            addAuditableParams(params);
            long artistId = simpleJdbcInsert.executeAndReturnKey(params).longValue();
            artists.put(nameAsKey, artistId);
            return artistId;
        }
    }

    private Long getAlbumId(Long artistId, AudioFile audioFile) {
        String name = getAlbumName(audioFile);
        AlbumArtistKey artistAlbumKey = new AlbumArtistKey(artistId, EntityNameHelper.toLookUpName(name));

        if (artistAlbums.containsKey(artistAlbumKey)) {
            return artistAlbums.get(artistAlbumKey);
        } else {
            SimpleJdbcInsert albumJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
            albumJdbcInsert.withTableName(AlbumEntity.TABLE_NAME).usingGeneratedKeyColumns(AlbumEntity.COL_PK_ID);
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue(AlbumEntity.COL_NAME, name)
                    .addValue(AlbumEntity.COL_SIMPLE_NAME, artistAlbumKey.getAlbumLoweCaseName())
                    .addValue(AlbumEntity.COL_ARTIST_ID, artistId);
            if (audioFile.getTag() != null) {
                Tag tag = audioFile.getTag();
                params
                        .addValue(AlbumEntity.COL_GENRE, tag.getFirst(FieldKey.GENRE))
                        .addValue(AlbumEntity.COL_DATE, tag.getFirst(FieldKey.YEAR))
                        .addValue(AlbumEntity.COL_MB_RELEASE_ID, tag.getFirst(FieldKey.MUSICBRAINZ_RELEASEID));
            }
            addAuditableParams(params);
            Long albumId = albumJdbcInsert.executeAndReturnKey(params).longValue();
            artistAlbums.put(artistAlbumKey, albumId);
            return albumId;
        }
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

    private void deleteAllTAbles() {
        TABLES.forEach(tableName -> {
            jdbcTemplate.execute(String.format("TRUNCATE TABLE %s", tableName));
        });
    }

    private static void addAuditableParams(MapSqlParameterSource params) {
        params.addValue(PersistentAuditableEntity.COL_CREATED_BY, "system");
        params.addValue(PersistentAuditableEntity.COL_CREATED_DATE, LocalDateTime.now());
    }
}
