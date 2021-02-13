package com.bemonovoid.playqd.core.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.dao.LibraryDao;
import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.Artwork;
import com.bemonovoid.playqd.core.model.ArtworkOnlineSearchResult;
import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.model.event.ArtworkResultReceived;
import com.bemonovoid.playqd.core.model.query.ArtworkLocalSearchQuery;
import com.bemonovoid.playqd.core.model.query.ArtworkOnlineSearchQuery;
import com.bemonovoid.playqd.core.service.ArtworkSearchService;
import com.bemonovoid.playqd.core.service.ArtworkService;
import com.bemonovoid.playqd.core.service.BinaryResourceProducer;
import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.StandardArtwork;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class ArtworkServiceImpl implements ArtworkService {

    private final LibraryDao libraryDao;
    private final ArtworkSearchService artworkSearchService;
    private final BinaryResourceProducer binaryResourceProducer;

    private final ApplicationEventPublisher publisher;

    ArtworkServiceImpl(ApplicationEventPublisher publisher,
                       LibraryDao libraryDao,
                       ArtworkSearchService artworkSearchService,
                       BinaryResourceProducer binaryResourceProducer) {
        this.publisher = publisher;
        this.libraryDao = libraryDao;
        this.artworkSearchService = artworkSearchService;
        this.binaryResourceProducer = binaryResourceProducer;
    }

    @Override
    public Artwork getArtworkFromLibrary(ArtworkLocalSearchQuery query) {
        if (query.getAlbumId() != null) {
            Optional<Album> albumOpt = libraryDao.ofAlbum().getOne(query.getAlbumId());
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

        ArtworkOnlineSearchResult artworkOnlineSearchResult = artworkSearchResultOpt.get();

        updateAlbumArtwork(album.getId(), artworkOnlineSearchResult.getImageUrl());

        return Optional.of(artworkOnlineSearchResult.getImageUrl());
    }

    public void updateAlbumArtwork(long albumId, String resourceUrl) {
        org.jaudiotagger.tag.images.Artwork artwork = buildArtworkTagFromResource(resourceUrl);
        List<Song> albumSongs = libraryDao.ofSong().getAlbumSongs(albumId);
        boolean setBinaryOnAlbum = false;
        for (Song song : albumSongs) {
            if (!setArtworkTagToAudioFile(song.getFileLocation(), artwork)) {
                setBinaryOnAlbum = true;
            }
        }
        if (setBinaryOnAlbum) {
            libraryDao.ofAlbum().setArtworkBinary(albumId, artwork.getBinaryData());
        }
    }

    private org.jaudiotagger.tag.images.Artwork buildArtworkTagFromResource(String url) {
        byte[] binaryData = binaryResourceProducer.toBinary(url);
        org.jaudiotagger.tag.images.Artwork artwork = new StandardArtwork();
        artwork.setImageUrl(url);
        artwork.setBinaryData(binaryData);
        return artwork;
    }

    private boolean setArtworkTagToAudioFile(String fileLocation, org.jaudiotagger.tag.images.Artwork artwork) {
        try {
            AudioFile audioFile = AudioFileIO.read(new File(fileLocation));
            Tag tag = audioFile.getTag();
            tag.setField(artwork);
            audioFile.commit();
            log.info("Artwork tag was successfully committed to audio file {}", fileLocation);
            return true;
        } catch (Exception e) {
            log.error(String.format("Failed to commit Artwork tag to audio file %s", fileLocation), e);
            return false;
        }
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
