package com.bemonovoid.playqd.core.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import com.bemonovoid.playqd.core.dao.LibraryDao;
import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.Artwork;
import com.bemonovoid.playqd.core.model.ArtworkOnlineSearchResult;
import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.model.event.AlbumTagsUpdated;
import com.bemonovoid.playqd.core.model.event.ArtistTagsUpdated;
import com.bemonovoid.playqd.core.model.query.ArtworkLocalSearchQuery;
import com.bemonovoid.playqd.core.model.query.ArtworkOnlineSearchQuery;
import com.bemonovoid.playqd.core.service.ArtworkSearchService;
import com.bemonovoid.playqd.core.service.ArtworkService;
import com.bemonovoid.playqd.core.service.BinaryResourceProducer;
import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
class ArtworkServiceImpl implements ArtworkService {

    private final LibraryDao libraryDao;
    private final ApplicationEventPublisher eventPublisher;
    private final ArtworkSearchService artworkSearchService;
    private final BinaryResourceProducer binaryResourceProducer;

    ArtworkServiceImpl(LibraryDao libraryDao,
                       ApplicationEventPublisher eventPublisher,
                       ArtworkSearchService artworkSearchService,
                       BinaryResourceProducer binaryResourceProducer) {
        this.libraryDao = libraryDao;
        this.eventPublisher = eventPublisher;
        this.artworkSearchService = artworkSearchService;
        this.binaryResourceProducer = binaryResourceProducer;
    }

    @Override
    public Artwork getArtworkFromLibrary(ArtworkLocalSearchQuery query) {
        if (query.getAlbumId() != null) {
            Optional<Album> albumOpt = libraryDao.ofAlbum().findOne(query.getAlbumId());
            if (albumOpt.isEmpty()) {
                return getDefault();
            }

            Album album = albumOpt.get();
            if (album.getArtwork() != null && album.getArtwork().getBinary() != null) {
                return album.getArtwork();
            } else {
                Song albumSong = getSong(query).get();
                return getArtworkFromAudioFile(albumSong.getFileLocation());
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

        ArtworkOnlineSearchResult searchResult = artworkSearchResultOpt.get();

        if (StringUtils.hasText(searchResult.getMbArtistId())) {
            eventPublisher.publishEvent(new ArtistTagsUpdated(this, Artist.builder()
                    .id(album.getArtist().getId())
                    .mbArtistId(searchResult.getMbArtistId())
                    .country(searchResult.getMbArtistCountry())
                    .build()));
        }

        if (StringUtils.hasText(searchResult.getMbReleaseId())) {
            eventPublisher.publishEvent(new AlbumTagsUpdated(this, Album.builder()
                    .id(album.getId()).mbReleaseId(searchResult.getMbReleaseId()).build()));
        }

        updateAlbumArtwork(album.getId(), searchResult.getImageUrl());

        return Optional.of(searchResult.getImageUrl());
    }

    @Override
    public void updateAlbumArtwork(long albumId, String src) {
        Artwork artwork = Artwork.builder().src(src).binary(binaryResourceProducer.toBinary(src)).build();
        eventPublisher.publishEvent(new AlbumTagsUpdated(this, Album.builder().artwork(artwork).build()));
        libraryDao.ofAlbum().setArtworkBinary(albumId, artwork.getBinary());
    }

    private Artwork getArtworkFromAudioFile(String songFileLocation) {
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
            log.error("Failed to retrieve an audio file for path {}. Default artwork will be used", songFileLocation);
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
            return libraryDao.ofSong().getFirstSongInAlbum(query.getAlbumId());
        } else {
            return libraryDao.ofSong().getOne(query.getSongId());
        }
    }
}
