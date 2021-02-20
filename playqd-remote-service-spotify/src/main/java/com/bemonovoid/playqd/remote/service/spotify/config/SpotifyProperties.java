package com.bemonovoid.playqd.remote.service.spotify.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
public class SpotifyProperties {

    private String apiBaseUrl = "https://api.spotify.com";
    private String accountBaseUrl = "https://accounts.spotify.com";
    private String apiVersion = "v1";
    private String clientId;
    private String clientSecret;
}
