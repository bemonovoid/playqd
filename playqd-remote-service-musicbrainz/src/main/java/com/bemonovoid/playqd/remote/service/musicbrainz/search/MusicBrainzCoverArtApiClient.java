package com.bemonovoid.playqd.remote.service.musicbrainz.search;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.bemonovoid.playqd.remote.service.musicbrainz.model.api.MBCoverArtQueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class MusicBrainzCoverArtApiClient {

    private final RestTemplate restTemplate;

    public MusicBrainzCoverArtApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<MBCoverArtQueryResponse> getCoverArt(String mbReleaseId) {

        log.info("Searching artowork by releaseId: {}", mbReleaseId);

        Map<String, Object> pathVariables = Map.of("mbReleaseId", mbReleaseId);
        String uriString = UriComponentsBuilder.fromPath("/release/{mbReleaseId}")
                .buildAndExpand(pathVariables)
                .toUriString();
        try {
            ResponseEntity<MBCoverArtQueryResponse> response = Executors.newSingleThreadScheduledExecutor().schedule(
                    () -> restTemplate.getForEntity(uriString, MBCoverArtQueryResponse.class),
                    500,
                    TimeUnit.MILLISECONDS)
                    .get();

            MBCoverArtQueryResponse responseBody = response.getBody();

            if (response.hasBody() && responseBody != null) {

                log.info("Found {} artworks(s) for releaseId: {}", responseBody.getImages().size(), mbReleaseId);

                return Optional.of(responseBody);
            } else {

                log.warn("No artworks for releaseId: {} were found", mbReleaseId);

                return Optional.empty();
            }
        } catch (InterruptedException | ExecutionException | HttpStatusCodeException e) {
            log.error(String.format("Failed to execute search query for artwork by releaseId: %s", mbReleaseId), e);
            return Optional.empty();
        }
    }
}
