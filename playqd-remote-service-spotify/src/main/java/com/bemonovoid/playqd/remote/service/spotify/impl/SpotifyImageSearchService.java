package com.bemonovoid.playqd.remote.service.spotify.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.Image;
import com.bemonovoid.playqd.core.model.Dimensions;
import com.bemonovoid.playqd.core.service.BinaryResourceReader;
import com.bemonovoid.playqd.core.service.ImageSearchService;
import com.bemonovoid.playqd.remote.service.spotify.model.api.SpotifyArtistItem;
import com.bemonovoid.playqd.remote.service.spotify.model.api.SpotifySearchArtistResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class SpotifyImageSearchService implements ImageSearchService {

    private final SpotifyApi spotifyApi;
    private final BinaryResourceReader binaryResourceReader;

    SpotifyImageSearchService(SpotifyApi spotifyApi, BinaryResourceReader binaryResourceReader) {
        this.spotifyApi = spotifyApi;
        this.binaryResourceReader = binaryResourceReader;
    }

    @Override
    public List<Image> searchArtistImage(Artist artist) {
        SpotifySearchArtistResponse response = spotifyApi.searchArtistByName(artist.getName());
        if (!response.getArtists().getItems().isEmpty()) {
            SpotifyArtistItem spotifyArtist = response.getArtists().getItems().get(0);
            return spotifyArtist.getImages().stream()
                .map(img -> {
                    Dimensions dimensions = new Dimensions(img.getHeight(), img.getWidth());
                    byte[] data = binaryResourceReader.read(img.getUrl());
                    return new Image(img.getUrl(), data, dimensions);
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
