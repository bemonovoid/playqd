package com.bemonovoid.playqd.remote.service.spotify.impl;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

import com.bemonovoid.playqd.core.exception.PlayqdImageServiceException;
import com.bemonovoid.playqd.remote.service.spotify.config.SpotifyProperties;
import com.bemonovoid.playqd.remote.service.spotify.model.api.SpotifyArtistAlbumsResponse;
import com.bemonovoid.playqd.remote.service.spotify.model.api.SpotifyRefreshTokenResponse;
import com.bemonovoid.playqd.remote.service.spotify.model.api.SpotifySearchAlbumResponse;
import com.bemonovoid.playqd.remote.service.spotify.model.api.SpotifySearchArtistResponse;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class SpotifyApi {

    private final SpotifyProperties properties;
    private final String searchApiBaseUrl;

    private LocalDateTime tokenExpirationDate;
    private char[] accessToken = new char[0];

    public SpotifyApi(SpotifyProperties properties) {
        this.properties = properties;
        this.searchApiBaseUrl = properties.getApiBaseUrl() + "/" + properties.getApiVersion() + "/search/";
    }

    SpotifySearchArtistResponse searchArtistByName(String artistName) {
        String httpUrl = String.format("%s?q=%s&type=artist", searchApiBaseUrl, artistName);

        RestTemplate restTemplate = new RestTemplateBuilder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken()).build();
        try {
            ResponseEntity<SpotifySearchArtistResponse> response =
                    restTemplate.getForEntity(httpUrl, SpotifySearchArtistResponse.class);

            SpotifySearchArtistResponse responseBody = response.getBody();

            if (responseBody == null) {
                throw new PlayqdImageServiceException(
                        String.format("Spotify search api query response is null. %s", httpUrl));
            }

            return responseBody;
        } catch (RestClientException e) {
            throw new PlayqdImageServiceException("Failed to perform spotify api query", e);
        }
    }

    SpotifyArtistAlbumsResponse searchArtistAlbums(String artistId) {
        UriComponents uriComponents = UriComponentsBuilder.fromPath("/artists/{artistId}/albums")
                .queryParam("limit", 50)
                .buildAndExpand(Map.of("artistId", artistId));
        RestTemplate restTemplate = new RestTemplateBuilder()
                .rootUri(properties.getApiBaseUrl() + "/" + properties.getApiVersion())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .build();
        ResponseEntity<SpotifyArtistAlbumsResponse> response =
                restTemplate.getForEntity(uriComponents.toUriString(), SpotifyArtistAlbumsResponse.class);
        return response.getBody();
    }

    SpotifySearchAlbumResponse searchArtistAlbum(String artistName, String albumName) {
        String artistNameEncoded = URLEncoder.encode(artistName, StandardCharsets.UTF_8);
        String albumNameEncoded = URLEncoder.encode(albumName, StandardCharsets.UTF_8);
        RestTemplate restTemplate = new RestTemplateBuilder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .build();
        try {
            String httpUrl = String.format("%s?q=album:%s+artist:%s&type=album",
                    searchApiBaseUrl, albumNameEncoded, artistNameEncoded);

            ResponseEntity<SpotifySearchAlbumResponse> response = restTemplate.getForEntity(
                    URI.create(httpUrl), SpotifySearchAlbumResponse.class);

            SpotifySearchAlbumResponse responseBody = response.getBody();

            if (responseBody == null) {
                throw new PlayqdImageServiceException(
                        String.format("Spotify search api query response is null. %s", httpUrl));
            }

            return responseBody;
        } catch (RestClientException e) {
            throw new PlayqdImageServiceException("Failed to perform spotify search api query", e);
        }
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
