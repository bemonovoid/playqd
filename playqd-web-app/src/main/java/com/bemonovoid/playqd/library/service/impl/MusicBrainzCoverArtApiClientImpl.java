package com.bemonovoid.playqd.library.service.impl;

import java.util.Map;
import java.util.Optional;

import com.bemonovoid.playqd.library.model.musicbrainz.MBCoverArtQueryResponse;
import com.bemonovoid.playqd.library.service.MusicBrainzCoverArtApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class MusicBrainzCoverArtApiClientImpl implements MusicBrainzCoverArtApiClient {

    private final static Logger LOG = LoggerFactory.getLogger(MusicBrainzCoverArtApiClientImpl.class);

    private final RestTemplate restTemplate;

    public MusicBrainzCoverArtApiClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<MBCoverArtQueryResponse> getCoverArt(String mbReleaseId) {
        Map<String, Object> pathVariables = Map.of("mbReleaseId", mbReleaseId);
        String uriString = UriComponentsBuilder.fromPath("/release/{mbReleaseId}")
                .buildAndExpand(pathVariables)
                .toUriString();
        try {
            ResponseEntity<MBCoverArtQueryResponse> response =
                    restTemplate.getForEntity(uriString, MBCoverArtQueryResponse.class);
            if (response.hasBody() && response.getBody() != null) {
                return Optional.of(response.getBody());
            } else {
                return Optional.empty();
            }
        } catch (HttpStatusCodeException e) {
            LOG.error("Failed to get cover art", e);
            return Optional.empty();
        }
    }
}
