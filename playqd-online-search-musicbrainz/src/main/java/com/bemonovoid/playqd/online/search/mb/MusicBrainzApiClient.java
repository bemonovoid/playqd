package com.bemonovoid.playqd.online.search.mb;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.bemonovoid.playqd.online.search.mb.model.api.MBArtistQueryResponse;
import com.bemonovoid.playqd.online.search.mb.model.api.MBArtistReleasesQueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class MusicBrainzApiClient  {

    private final static Logger LOG = LoggerFactory.getLogger(MusicBrainzApiClient.class);

    private final RestTemplate restTemplate;

    public MusicBrainzApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<MBArtistQueryResponse> getArtist(String artistName) {

        LOG.info("Searching artist by {}", artistName);

        String uriString = UriComponentsBuilder.fromPath("/artist")
                .queryParam("query", String.format("\"%s\"", artistName))
                .queryParam("fmt", "json")
                .build().toUriString();
        try {

            ResponseEntity<MBArtistQueryResponse> response = Executors.newSingleThreadScheduledExecutor().schedule(
                    () -> restTemplate.getForEntity(uriString, MBArtistQueryResponse.class),
                    500,
                    TimeUnit.MILLISECONDS)
                    .get();

            MBArtistQueryResponse responseBody = response.getBody();

            if (response.hasBody() && responseBody != null) {

                LOG.info("Found {} artist(s) with name like: {}", responseBody.getArtists().size(), artistName);

                return Optional.of(responseBody);
            }

            LOG.warn("No artists with name like: {} were found", artistName);

            return Optional.empty();
        } catch (InterruptedException | ExecutionException | HttpStatusCodeException e) {
            LOG.error(String.format("Failed to execute search query for artist: %s", artistName), e);
            return Optional.empty();
        }
    }

    public Optional<MBArtistReleasesQueryResponse> getReleases(String mbArtistId) {

        LOG.info("Searching artist releases by {}", mbArtistId);

        Map<String, Object> pathVariables = Map.of("mbArtistId", mbArtistId);
        String uriString = UriComponentsBuilder.fromPath("/artist/{mbArtistId}")
                .queryParam("inc", "releases")
                .queryParam("fmt", "json")
                .buildAndExpand(pathVariables)
                .toUriString();
        try {
            ResponseEntity<MBArtistReleasesQueryResponse> response =
                    Executors.newSingleThreadScheduledExecutor().schedule(
                            () -> restTemplate.getForEntity(uriString, MBArtistReleasesQueryResponse.class),
                            500,
                            TimeUnit.MILLISECONDS)
                            .get();

            MBArtistReleasesQueryResponse responseBody = response.getBody();

            if (response.hasBody() && responseBody != null) {

                LOG.info("Found {} release(s) for artist: id={}; name={}",
                        responseBody.getReleases().size(), responseBody.getId(), responseBody.getName());

                return Optional.of(responseBody);
            }
            LOG.warn("No releases for artistId: {} were found", mbArtistId);
            return Optional.empty();
        } catch (InterruptedException | ExecutionException | HttpStatusCodeException e) {
            LOG.error(String.format("Failed to execute search query for releases by artistId: %s", mbArtistId), e);
            return Optional.empty();
        }
    }
}
