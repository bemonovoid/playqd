package com.bemonovoid.playqd.remote.service.spotify.impl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

import com.bemonovoid.playqd.remote.service.spotify.config.SpotifyProperties;
import com.bemonovoid.playqd.remote.service.spotify.model.api.SpotifyArtistAlbumsResponse;
import com.bemonovoid.playqd.remote.service.spotify.model.api.SpotifyRefreshTokenResponse;
import com.bemonovoid.playqd.remote.service.spotify.model.api.SpotifySearchArtistResponse;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class SpotifyApi {

    private final SpotifyProperties properties;
    private LocalDateTime tokenExpirationDate;
    private char[] accessToken = new char[0];

    public SpotifyApi(SpotifyProperties properties) {
        this.properties = properties;
    }

    SpotifySearchArtistResponse searchArtistByName(String artistName) {
        UriComponents uriComponents = UriComponentsBuilder.fromPath("/search")
                .queryParam("q", String.format("\"%s\"", artistName))
                .queryParam("type", "artist")
                .build();
        RestTemplate restTemplate = new RestTemplateBuilder()
                .rootUri(properties.getApiBaseUrl() + "/" + properties.getApiVersion())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .build();
        ResponseEntity<SpotifySearchArtistResponse> response =
                restTemplate.getForEntity(uriComponents.toUriString(), SpotifySearchArtistResponse.class);
        return response.getBody();
    }

    SpotifyArtistAlbumsResponse searchArtistAlbums(String artistId) {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .rootUri(properties.getApiBaseUrl() + "/" + properties.getApiVersion())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .build();
        Map<String, String> pathVariables = Map.of("artistId", artistId);
        ResponseEntity<SpotifyArtistAlbumsResponse> response =
                restTemplate.getForEntity("/artists/{artistId}/albums", SpotifyArtistAlbumsResponse.class, pathVariables);
        return response.getBody();
    }

    private String getAccessToken() {
        String token = String.copyValueOf(accessToken);
        if (tokenExpirationDate == null || tokenExpirationDate.isBefore(LocalDateTime.now())) {

            RestTemplate restTemplate = new RestTemplateBuilder()
                    .rootUri(properties.getAccountBaseUrl())
                    .basicAuthentication(properties.getClientId(), properties.getClientSecret(), StandardCharsets.UTF_8)
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", "client_credentials");

            ResponseEntity<SpotifyRefreshTokenResponse> response = restTemplate.postForEntity(
                    "/api/token", new HttpEntity<>(map, headers), SpotifyRefreshTokenResponse.class);

            SpotifyRefreshTokenResponse tokenInfo = response.getBody();

            tokenExpirationDate = LocalDateTime.now().plusSeconds(tokenInfo.getExpiresIn());

            token = tokenInfo.getAccessToken();
            accessToken = token.toCharArray();
        }
        return token;
    }
}
