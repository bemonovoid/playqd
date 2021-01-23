package com.bemonovoid.playqd.library.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import com.bemonovoid.playqd.core.ArtworkLocalQuery;
import com.bemonovoid.playqd.data.dao.AlbumDao;
import com.bemonovoid.playqd.data.dao.SongDao;
import com.bemonovoid.playqd.data.entity.AlbumEntity;
import com.bemonovoid.playqd.data.entity.ArtworkStatus;
import com.bemonovoid.playqd.data.entity.SongEntity;
import com.bemonovoid.playqd.library.service.ArtworkService;
import com.bemonovoid.playqd.library.service.events.ArtworkResultReceived;
import com.bemonovoid.playqd.online.search.ArtworkBinary;
import com.bemonovoid.playqd.online.search.ArtworkOnlineSearchService;
import com.bemonovoid.playqd.online.search.ArtworkSearchFilter;
import com.bemonovoid.playqd.online.search.ArtworkSearchResult;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;

public class ArtworkServiceImpl implements ArtworkService {

    private static final Logger LOG = LoggerFactory.getLogger(ArtworkServiceImpl.class);

    private final SongDao songDao;
    private final AlbumDao albumDao;
    private final ArtworkOnlineSearchService artworkSearchService;

    private final ApplicationEventPublisher publisher;

    public ArtworkServiceImpl(SongDao songDao,
                              AlbumDao albumDao,
                              ArtworkOnlineSearchService artworkSearchService,
                              ApplicationEventPublisher publisher) {
        this.songDao = songDao;
        this.albumDao = albumDao;
        this.artworkSearchService = artworkSearchService;
        this.publisher = publisher;
    }

    @Override
    public ArtworkBinary getBinaryFromLocalLibrary(ArtworkLocalQuery query) {
        if (query.getAlbumId() != null) {
            Optional<AlbumEntity> albumEntityOpt = albumDao.getOne(query.getAlbumId());
            if (albumEntityOpt.isEmpty()) {
                return getDefault();
            }

            AlbumEntity albumEntity = albumEntityOpt.get();

            if (ArtworkStatus.AVAILABLE == albumEntity.getArtworkStatus()) {
                return getArtworkFromFile(albumEntity.getSongs().get(0));
            } else {
                return getDefault();
            }
        }
        return getDefault();
    }

    @Override
    public Optional<String> searchOnline(ArtworkLocalQuery localQuery) {
        Optional<SongEntity> songEntityOpt = getSongEntity(localQuery);

        if (songEntityOpt.isEmpty()){
            return Optional.empty();
        }

        SongEntity songEntity = songEntityOpt.get();
        AlbumEntity albumEntity = songEntity.getAlbum();

        if (ArtworkStatus.UNKNOWN != albumEntity.getArtworkStatus()) {
            return Optional.empty();
        }

        ArtworkSearchFilter searchFilter = ArtworkSearchFilter.builder()
                .artistName(songEntity.getArtist().getSimpleName())
                .mbArtistId(songEntity.getArtist().getMbArtistId())
                .albumName(albumEntity.getSimpleName())
                .build();

        Optional<ArtworkSearchResult> artworkSearchResultOpt = artworkSearchService.search(searchFilter);

        if (artworkSearchResultOpt.isEmpty()) {
            albumEntity.setArtworkStatus(ArtworkStatus.UNAVAILABLE);
            albumDao.save(albumEntity);
            return Optional.empty();
        }

        ArtworkSearchResult artworkSearchResult = artworkSearchResultOpt.get();

        publisher.publishEvent(new ArtworkResultReceived(this, albumEntity.getId(), artworkSearchResult));

        return Optional.of(artworkSearchResult.getImageUrl());
    }

    private ArtworkBinary getArtworkFromFile(SongEntity songEntity) {
        File file = new File(songEntity.getFileLocation());
        try {
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();
            if (tag != null) {
                Artwork artwork = tag.getFirstArtwork();
                if (artwork != null) {
                    return new ArtworkBinary(artwork.getBinaryData(), artwork.getMimeType());
                } else if (songEntity.getAlbum().getArtworkBinary() != null) {
                    return new ArtworkBinary(songEntity.getAlbum().getArtworkBinary(), "image/jpeg");
                }
            }
            throw new IllegalArgumentException("Failed to retrieve artwork");
        } catch (Exception e) {
            LOG.error("Failed to retrieve an audio file for path {}", songEntity.getFileLocation());
            throw new RuntimeException(e);
        }
    }

    private static ArtworkBinary getDefault() {
        try (InputStream is = new ClassPathResource("/public/images/default-album-cover.png").getInputStream()) {
            return new ArtworkBinary(is.readAllBytes(), "image/png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<SongEntity> getSongEntity(ArtworkLocalQuery query) {
        if (query.getAlbumId() != null) {
            return songDao.getFirstSongInAlbum(query.getAlbumId());
        } else {
            return songDao.getOne(query.getSongId());
        }
    }
}
