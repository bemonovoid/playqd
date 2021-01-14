package com.bemonovoid.playqd.library.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.bemonovoid.playqd.data.dao.SongDao;
import com.bemonovoid.playqd.data.entity.ArtworkStatus;
import com.bemonovoid.playqd.library.model.musicbrainz.MBArtworkInfo;
import com.bemonovoid.playqd.library.service.MusicBrainzService;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.StandardArtwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

class UpdateArtworkJob {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateArtworkJob.class);

    private final JdbcTemplate jdbcTemplate;
    private final SongDao songDao;
    private final MusicBrainzService musicBrainzService;

    private final Queue<Long> songs = new LinkedBlockingQueue<>();
    private final ScheduledExecutorService scheduledExecutorService;

    UpdateArtworkJob(JdbcTemplate jdbcTemplate, SongDao songDao, MusicBrainzService musicBrainzService) {
        this.jdbcTemplate = jdbcTemplate;
        this.songDao = songDao;
        this.musicBrainzService = musicBrainzService;
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(this::execute, 3, 3, TimeUnit.SECONDS);
    }

    void updateNext(long songId) {
        songs.offer(songId);
        LOG.info("Added song with id: {} to artwork update queue", songId);
    }

    private void execute() {

        Long nextSongId = songs.poll();
        if (nextSongId == null) {
            LOG.info("Nothing to update. Queue is empty");
            return;
        }

        SongInfo songInfo = getArtistAlbumNames(nextSongId);

        if (ArtworkStatus.UNAVAILABLE == songInfo.artworkStatus) {
            LOG.warn("Artwork is unavailable");
            return;
        }

        LOG.info("Resolving artwork");

        Optional<MBArtworkInfo> mayBeMbArtworkInfo =
                musicBrainzService.getArtworkInfo(songInfo.artistName, songInfo.albumName);

        if (mayBeMbArtworkInfo.isEmpty()) {
            setArtworkStatus(nextSongId, ArtworkStatus.UNAVAILABLE);
            return;
        }

        MBArtworkInfo mbArtworkInfo = mayBeMbArtworkInfo.get();

        byte[] binaryData = new RestTemplate().getForObject(mbArtworkInfo.getImageUrl(), byte[].class);

        try {
            Artwork artwork = new StandardArtwork();
            artwork.setImageUrl(mbArtworkInfo.getImageUrl());
            artwork.setBinaryData(binaryData);

            for (Map.Entry<Long, String> entry : getAlbumSongsLocation(songInfo.albumId).entrySet()) {

                AudioFile audioFile = AudioFileIO.read(new File(entry.getValue()));

                Tag tag = audioFile.getTag();

                String existingMbArtistId = tag.getFirst(FieldKey.MUSICBRAINZ_ARTISTID);

                if (existingMbArtistId == null || existingMbArtistId.isBlank()) {
                    tag.setField(FieldKey.MUSICBRAINZ_ARTISTID, mbArtworkInfo.getMbArtistId());
                }

                String existingMbReleaseId = tag.getFirst(FieldKey.MUSICBRAINZ_RELEASEID);

                if (existingMbReleaseId == null || existingMbReleaseId.isBlank()) {
                    tag.setField(FieldKey.MUSICBRAINZ_RELEASEID, mbArtworkInfo.getMbReleaseId());
                }

                tag.setField(artwork);

                audioFile.commit();

                setArtworkStatus(entry.getKey(), ArtworkStatus.AVAILABLE);
            }
        } catch (Exception e) {
            LOG.error("Failed to update audio file tags", e);
//            return resolvedAlbumArtwork;
        }
    }

    private void setArtworkStatus(long songId, ArtworkStatus status) {
        jdbcTemplate.update(
                "UPDATE SONG SET ARTWORK_STATUS = ? WHERE ID = ?", status.name(), songId);
    }

    private SongInfo getArtistAlbumNames(long songId) {
        String sql = "SELECT artist.SIMPLE_NAME as ARTIST_NAME, album.SIMPLE_NAME as ALBUM_NAME, song.ALBUM_ID, song.ARTWORK_STATUS\n" +
                "FROM SONG song\n" +
                "INNER JOIN ARTIST artist on song.ARTIST_ID = artist.ID\n" +
                "INNER JOIN ALBUM album on song.ALBUM_ID = album.ID\n" +
                "WHERE song.ID = ?";
        SongInfo songInfo = jdbcTemplate.query(sql, (rs) -> {
            if (!rs.next()) {
                 return null;
            }
            SongInfo result = new SongInfo();
            result.artistName = rs.getString("ARTIST_NAME");
            result.albumId = rs.getLong("ALBUM_ID");
            result.albumName = rs.getString("ALBUM_NAME");
            result.artworkStatus = ArtworkStatus.valueOf(rs.getString("ARTWORK_STATUS"));
            return result;
        }, songId);
        return songInfo;
    }

    private Map<Long, String> getAlbumSongsLocation(long albumId) {
        return jdbcTemplate.query("SELECT ID, FILE_LOCATION FROM SONG where ALBUM_ID = ?", (rs) -> {
            Map<Long, String> locations = new HashMap<>();
            while (rs.next()) {
                locations.put(rs.getLong("ID"), rs.getString("FILE_LOCATION"));
            }
            return locations;
        }, albumId);
    }

    private class SongInfo {
        private String artistName;
        private long albumId;
        private String albumName;
        private ArtworkStatus artworkStatus;
    }
}
