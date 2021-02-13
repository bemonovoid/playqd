package com.bemonovoid.playqd.remote.service.musicbrainz.search;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.bemonovoid.playqd.remote.service.musicbrainz.model.api.MBArtistQueryResponse;
import com.bemonovoid.playqd.remote.service.musicbrainz.model.api.MBArtistReleasesQueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
class MusicBrainzApiClient  {

    private static final String ROOT_URI = "http://musicbrainz.org/ws/2";

    public Optional<MBArtistQueryResponse> getArtist(String artistName) {

        log.info("Searching artist by {}", artistName);

        String uriString = UriComponentsBuilder
                .fromUriString(ROOT_URI + "/artist")
                .queryParam("query", String.format("\"%s\"", artistName))
                .queryParam("fmt", "json")
                .build().toUriString();
        try {

            ResponseEntity<MBArtistQueryResponse> response = Executors.newSingleThreadScheduledExecutor().schedule(
                    () -> new RestTemplate().getForEntity(uriString, MBArtistQueryResponse.class),
                    500,
                    TimeUnit.MILLISECONDS)
                    .get();

            MBArtistQueryResponse responseBody = response.getBody();

            if (response.hasBody() && responseBody != null) {

                log.info("Found {} artist(s) with name like: {}", responseBody.getArtists().size(), artistName);

                return Optional.of(responseBody);
            }

            log.warn("No artists with name like: {} were found", artistName);

            return Optional.empty();
        } catch (InterruptedException | ExecutionException | HttpStatusCodeException e) {
            log.error(String.format("Failed to execute search query for artist: %s", artistName), e);
            return Optional.empty();
        }
    }

    public Optional<MBArtistReleasesQueryResponse> getReleases(String mbArtistId) {

        log.info("Searching artist releases by {}", mbArtistId);

        Map<String, Object> pathVariables = Map.of("mbArtistId", mbArtistId);
        String uriString = UriComponentsBuilder
                .fromUriString(ROOT_URI + "/artist/{mbArtistId}")
                .queryParam("inc", "releases")
                .queryParam("fmt", "json")
                .buildAndExpand(pathVariables)
                .toUriString();
        try {
            ResponseEntity<MBArtistReleasesQueryResponse> response =
                    Executors.newSingleThreadScheduledExecutor().schedule(
                            () -> new RestTemplate().getForEntity(uriString, MBArtistReleasesQueryResponse.class),
                            500,
                            TimeUnit.MILLISECONDS)
                            .get();

            MBArtistReleasesQueryResponse responseBody = response.getBody();

            if (response.hasBody() && responseBody != null) {

                log.info("Found {} release(s) for artist: id={}; name={}",
                        responseBody.getReleases().size(), responseBody.getId(), responseBody.getName());

                return Optional.of(responseBody);
            }
            log.warn("No releases for artistId: {} were found", mbArtistId);
            return Optional.empty();
        } catch (InterruptedException | ExecutionException | HttpStatusCodeException e) {
            log.error(String.format("Failed to execute search query for releases by artistId: %s", mbArtistId), e);
            return Optional.empty();
        }
    }
}
