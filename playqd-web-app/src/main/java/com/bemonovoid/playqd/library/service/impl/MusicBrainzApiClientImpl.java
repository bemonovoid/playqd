package com.bemonovoid.playqd.library.service.impl;

import java.util.Map;
import java.util.Optional;

import com.bemonovoid.playqd.library.model.musicbrainz.MBArtistQueryResponse;
import com.bemonovoid.playqd.library.model.musicbrainz.MBArtistReleasesQueryResponse;
import com.bemonovoid.playqd.library.service.MusicBrainzApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class MusicBrainzApiClientImpl implements MusicBrainzApiClient {

    private final static Logger LOG = LoggerFactory.getLogger(MusicBrainzApiClientImpl.class);

    private final RestTemplate restTemplate;

    public MusicBrainzApiClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<MBArtistQueryResponse> getArtist(String artistName) {
        String uriString = UriComponentsBuilder.fromPath("/artist")
                .queryParam("query", String.format("\"%s\"", artistName))
                .queryParam("fmt", "json")
                .build().toUriString();
        try {
            ResponseEntity<MBArtistQueryResponse> response =
                    restTemplate.getForEntity(uriString, MBArtistQueryResponse.class);
            if (response.hasBody() && response.getBody() != null) {
                return Optional.of(response.getBody());
            }
            return Optional.empty();
        } catch (HttpStatusCodeException e) {
            LOG.error("Failed to get artist", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<MBArtistReleasesQueryResponse> getReleases(String mbArtistId) {
        Map<String, Object> pathVariables = Map.of("mbArtistId", mbArtistId);
        String uriString = UriComponentsBuilder.fromPath("/artist/{mbArtistId}")
                .queryParam("inc", "releases")
                .queryParam("fmt", "json")
                .buildAndExpand(pathVariables)
                .toUriString();
        try {
            ResponseEntity<MBArtistReleasesQueryResponse> response =
                    restTemplate.getForEntity(uriString, MBArtistReleasesQueryResponse.class);
            if (response.hasBody() && response.getBody() != null) {
                return Optional.of(response.getBody());
            }
            return Optional.empty();
        } catch (HttpStatusCodeException e) {
            LOG.error("Failed to get release", e);
            return Optional.empty();
        }
    }
}
