package com.bemonovoid.playqd.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import com.bemonovoid.playqd.core.dao.AlbumDao;
import com.bemonovoid.playqd.core.dao.SongDao;
import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.Artwork;
import com.bemonovoid.playqd.core.model.ArtworkOnlineSearchQuery;
import com.bemonovoid.playqd.core.model.ArtworkOnlineSearchResult;
import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.model.query.ArtworkLocalSearchQuery;
import com.bemonovoid.playqd.core.service.ArtworkSearchService;
import com.bemonovoid.playqd.core.service.ArtworkService;
import com.bemonovoid.playqd.event.ArtworkResultReceived;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
class ArtworkServiceImpl implements ArtworkService {

    private static final Logger LOG = LoggerFactory.getLogger(ArtworkServiceImpl.class);

    private final SongDao songDao;
    private final AlbumDao albumDao;
    private final ArtworkSearchService artworkSearchService;

    private final ApplicationEventPublisher publisher;

    ArtworkServiceImpl(ApplicationEventPublisher publisher,
                       SongDao songDao,
                       AlbumDao albumDao,
                       ArtworkSearchService artworkSearchService) {
        this.publisher = publisher;
        this.songDao = songDao;
        this.albumDao = albumDao;
        this.artworkSearchService = artworkSearchService;
    }

    @Override
    public Artwork getArtworkFromLibrary(ArtworkLocalSearchQuery query) {
        if (query.getAlbumId() != null) {
            Optional<Album> albumOpt = albumDao.getOne(query.getAlbumId());
            if (albumOpt.isEmpty()) {
                return getDefault();
            }

            Album album = albumOpt.get();
            if (album.getArtwork() != null && album.getArtwork().getBinary() != null) {
                return album.getArtwork();
            } else {
                Song albumSong = getSong(query).get();
                return getArtworkFromFile(albumSong.getFileLocation());
            }
        }
        return getDefault();
    }

    @Override
    public Optional<String> getArtworkOnline(ArtworkLocalSearchQuery localQuery) {
        Optional<Song> songOpt = getSong(localQuery);

        if (songOpt.isEmpty()){
            return Optional.empty();
        }

        Song song = songOpt.get();
        Album album = song.getAlbum();

        ArtworkOnlineSearchQuery onlineSearchQuery = ArtworkOnlineSearchQuery.builder()
                .artistName(song.getArtist().getSimpleName())
                .mbArtistId(song.getArtist().getMbArtistId())
                .albumName(album.getSimpleName())
                .build();

        Optional<ArtworkOnlineSearchResult> artworkSearchResultOpt = artworkSearchService.search(onlineSearchQuery);

        if (artworkSearchResultOpt.isEmpty()) {
            return Optional.empty();
        }

        ArtworkOnlineSearchResult artworkOnlineSearchResult = artworkSearchResultOpt.get();

        publisher.publishEvent(new ArtworkResultReceived(this, album, artworkOnlineSearchResult));

        return Optional.of(artworkOnlineSearchResult.getImageUrl());
    }

    private Artwork getArtworkFromFile(String songFileLocation) {
        File file = new File(songFileLocation);
        try {
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();
            if (tag != null) {
                org.jaudiotagger.tag.images.Artwork artwork = tag.getFirstArtwork();
                if (artwork != null) {
                    return Artwork.builder().binary(artwork.getBinaryData()).mimeType(artwork.getMimeType()).build();
                }
            }
            return getDefault();
        } catch (Exception e) {
            LOG.error("Failed to retrieve an audio file for path {}. Default artwork will be used", songFileLocation);
            return getDefault();
        }
    }

    private static Artwork getDefault() {
        try (InputStream is = new ClassPathResource("/public/images/default-album-cover.png").getInputStream()) {
            return Artwork.builder().binary(is.readAllBytes()).mimeType("image/png").build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<Song> getSong(ArtworkLocalSearchQuery query) {
        if (query.getAlbumId() != null) {
            return songDao.getFirstSongInAlbum(query.getAlbumId());
        } else {
            return songDao.getOne(query.getSongId());
        }
    }
}
