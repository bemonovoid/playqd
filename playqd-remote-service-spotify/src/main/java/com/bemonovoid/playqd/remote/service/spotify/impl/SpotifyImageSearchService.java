package com.bemonovoid.playqd.remote.service.spotify.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.bemonovoid.playqd.core.dao.ArtistDao;
import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.Image;
import com.bemonovoid.playqd.core.model.Dimensions;
import com.bemonovoid.playqd.core.service.BinaryResourceReader;
import com.bemonovoid.playqd.core.service.ImageSearchService;
import com.bemonovoid.playqd.remote.service.spotify.model.api.SpotifyArtistAlbumsResponse;
import com.bemonovoid.playqd.remote.service.spotify.model.api.SpotifyLibraryItem;
import com.bemonovoid.playqd.remote.service.spotify.model.api.SpotifySearchArtistResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class SpotifyImageSearchService implements ImageSearchService {

    private final ArtistDao artistDao;
    private final SpotifyApi spotifyApi;
    private final BinaryResourceReader binaryResourceReader;

    SpotifyImageSearchService(ArtistDao artistDao,
                              SpotifyApi spotifyApi,
                              BinaryResourceReader binaryResourceReader) {
        this.artistDao = artistDao;
        this.spotifyApi = spotifyApi;
        this.binaryResourceReader = binaryResourceReader;
    }

    @Override
    public List<Image> searchArtistImage(Artist artist) {
        SpotifySearchArtistResponse response = spotifyApi.searchArtistByName(artist.getName());
        if (!response.getArtists().getItems().isEmpty()) {
            SpotifyLibraryItem spotifyArtist = response.getArtists().getItems().get(0);

            artistDao.setSpotifyArtistId(artist.getId(), spotifyArtist.getId());

            return spotifyArtist.getImages().stream()
                .map(img -> {
                    Dimensions dimensions = new Dimensions(img.getHeight(), img.getWidth());
                    byte[] data = binaryResourceReader.read(img.getUrl());
                    return new Image(img.getUrl(), data, dimensions);
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<Image> searchAlbumImage(Album album) {
        if (album.getArtist().getSpotifyId() == null) {
            return Collections.emptyList();
        }
        SpotifyArtistAlbumsResponse response = spotifyApi.searchArtistAlbums(album.getArtist().getSpotifyId());

        Optional<SpotifyLibraryItem> spotifyAlbum = response.getItems().stream()
                .filter(item -> albumNamePredicate(item.getName(), album.getSimpleName()))
                .findFirst();
        if (spotifyAlbum.isEmpty()) {
            return Collections.emptyList();
        }
        return spotifyAlbum.get().getImages().stream()
                .map(img -> {
                    Dimensions dimensions = new Dimensions(img.getHeight(), img.getWidth());
                    byte[] data = binaryResourceReader.read(img.getUrl());
                    return new Image(img.getUrl(), data, dimensions);
                }).collect(Collectors.toList());
    }

    private boolean albumNamePredicate(String spotifyAlbumName, String libraryAlbumName) {
        return spotifyAlbumName.equalsIgnoreCase(libraryAlbumName) ||
                spotifyAlbumName.toLowerCase().contains(libraryAlbumName);
    }

}
