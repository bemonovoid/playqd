package com.bemonovoid.playqd.library.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import com.bemonovoid.playqd.data.dao.SongDao;
import com.bemonovoid.playqd.data.entity.ArtworkStatus;
import com.bemonovoid.playqd.data.entity.SongEntity;
import com.bemonovoid.playqd.library.model.AlbumArtwork;
import com.bemonovoid.playqd.library.model.query.ArtworkQuery;
import com.bemonovoid.playqd.library.service.ArtworkService;
import com.bemonovoid.playqd.library.service.MusicBrainzService;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

public class ArtworkServiceImpl implements ArtworkService {

    private static final Logger LOG = LoggerFactory.getLogger(ArtworkServiceImpl.class);

    private final SongDao songDao;
//    private final UpdateArtworkJob updateArtworkJob;

    public ArtworkServiceImpl(JdbcTemplate jdbcTemplate, SongDao songDao, MusicBrainzService musicBrainzService) {
        this.songDao = songDao;
//        updateArtworkJob = new UpdateArtworkJob(jdbcTemplate, songDao, musicBrainzService);
    }

    @Override
    public AlbumArtwork get(ArtworkQuery query) {
        SongEntity songEntity;
        if (query.getAlbumId() != null) {
            Optional<SongEntity> mayBeSongEntity = songDao.getFirstSongInAlbum(query.getAlbumId());
            if (mayBeSongEntity.isEmpty()) {
                return getDefault();
            }
            songEntity = mayBeSongEntity.get();
        } else {
            Optional<SongEntity> mayBeSongEntity = songDao.getOne(query.getSongId());
            if (mayBeSongEntity.isEmpty()) {
                return getDefault();
            }
            songEntity = mayBeSongEntity.get();
        }
        if (ArtworkStatus.AVAILABLE == songEntity.getArtworkStatus()) {
            return getArtworkFromFile(songEntity);
        } else if (ArtworkStatus.UNAVAILABLE == songEntity.getArtworkStatus()) {
            return getDefault();
        } else {
//            updateArtworkJob.updateNext(songEntity.getId());
            return getDefault();
        }
    }

    private AlbumArtwork getArtworkFromFile(SongEntity songEntity) {
        File file = new File(songEntity.getFileLocation());
        try {
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();
            if (tag != null) {
                Artwork artwork = tag.getFirstArtwork();
                if (artwork != null) {
                    return new AlbumArtwork(artwork.getBinaryData(), artwork.getMimeType());
                }
            }
            throw new IllegalArgumentException("Failed to retrieve artwork");
        } catch (Exception e) {
            LOG.error("Failed to retrieve an audio file for path {}", songEntity.getFileLocation());
            throw new RuntimeException(e);
        }
    }

    private static AlbumArtwork getDefault() {
        try (InputStream is = new ClassPathResource("/public/images/default-album-cover.png").getInputStream()) {
            return new AlbumArtwork(is.readAllBytes(), "image/png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
