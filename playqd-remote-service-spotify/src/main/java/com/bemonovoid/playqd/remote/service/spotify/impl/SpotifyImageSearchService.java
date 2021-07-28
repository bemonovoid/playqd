package com.bemonovoid.playqd.remote.service.spotify.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.bemonovoid.playqd.core.dao.ArtistDao;
import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.Dimensions;
import com.bemonovoid.playqd.core.model.Image;
import com.bemonovoid.playqd.core.service.BinaryResourceClient;
import com.bemonovoid.playqd.core.service.ImageSearchService;
import com.bemonovoid.playqd.remote.service.spotify.model.api.SpotifyAlbumItem;
import com.bemonovoid.playqd.remote.service.spotify.model.api.SpotifyArtistAlbumsResponse;
import com.bemonovoid.playqd.remote.service.spotify.model.api.SpotifyLibraryItem;
import com.bemonovoid.playqd.remote.service.spotify.model.api.SpotifySearchAlbumResponse;
import com.bemonovoid.playqd.remote.service.spotify.model.api.SpotifySearchArtistResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpotifyImageSearchService implements ImageSearchService {

    private final ArtistDao artistDao;
    private final SpotifyApi spotifyApi;
    private final BinaryResourceClient binaryResourceClient;

    public SpotifyImageSearchService(SpotifyApi spotifyApi,
                              ArtistDao artistDao,
                              BinaryResourceClient binaryResourceClient) {
        this.artistDao = artistDao;
        this.spotifyApi = spotifyApi;
        this.binaryResourceClient = binaryResourceClient;
    }

    @Override
    public List<Image> searchArtistImage(Artist artist) {
        log.info("Searching image for the artist with id: {}", artist.getId());

        return findSpotifyArtist(artist)
                .map(spotifyLibraryItem -> spotifyLibraryItem.getImages().stream()
                        .map(img -> {
                            Dimensions dimensions = new Dimensions(img.getHeight(), img.getWidth());
//                            byte[] data = binaryResourceClient.get(img.getUrl());
                            return new Image(img.getUrl(), null, dimensions);
                        }).collect(Collectors.toList()))
                .orElseGet(Collections::emptyList);
    }

    @Override
    public List<Image> searchAlbumImage(Album album) {
        SpotifyAlbumItem spotifyAlbum;
        if (album.getArtist().getSpotifyId() != null) {
            SpotifyArtistAlbumsResponse response = spotifyApi.searchArtistAlbums(album.getArtist().getSpotifyId());
            String expectedAlbumName = album.getName().toLowerCase();
            Optional<SpotifyAlbumItem> spotifyAlbumOpt = response.getItems().stream()
                    .filter(item -> {
                        String actualAlbumName = item.getName().toLowerCase();
                        return actualAlbumName.equalsIgnoreCase(expectedAlbumName) ||
                                actualAlbumName.contains(expectedAlbumName);
                    })
                    .findFirst();
            if (spotifyAlbumOpt.isEmpty()) {
                return Collections.emptyList();
            }
            spotifyAlbum = spotifyAlbumOpt.get();
        } else {
            SpotifySearchAlbumResponse response = spotifyApi.searchArtistAlbum(album.getArtist().getName(), album.getName());

            Optional<SpotifyAlbumItem> spotifyAlbumOpt = response.getAlbums().getItems().stream()
                    .findFirst();
            if (spotifyAlbumOpt.isEmpty()) {
                return Collections.emptyList();
            }
            spotifyAlbum = spotifyAlbumOpt.get();
        }
        return spotifyAlbum.getImages().stream()
                .map(img -> {
                    Dimensions dimensions = new Dimensions(img.getHeight(), img.getWidth());
                    byte[] data = binaryResourceClient.get(img.getUrl());
                    return new Image(img.getUrl(), data, dimensions);
                }).collect(Collectors.toList());

    }

    private Optional<SpotifyLibraryItem> findSpotifyArtist(Artist artist) {
        SpotifyLibraryItem spotifyLibraryItem;
        if (artist.getSpotifyId() != null) {
            spotifyLibraryItem = spotifyApi.searchArtistById(artist.getSpotifyId());

        } else {
            SpotifySearchArtistResponse response = spotifyApi.searchArtistByName(artist.getName());
            if (response.getArtists().getItems().isEmpty()) {
                return Optional.empty();
            }
            spotifyLibraryItem = response.getArtists().getItems().get(0);
        }

        artistDao.update(Artist.builder()
                .id(artist.getId())
                .spotifyId(spotifyLibraryItem.getId())
                .build());

        return Optional.of(spotifyLibraryItem);
    }

}
