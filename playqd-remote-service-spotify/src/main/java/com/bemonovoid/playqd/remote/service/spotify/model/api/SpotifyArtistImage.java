package com.bemonovoid.playqd.remote.service.spotify.model.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
public class SpotifyArtistImage {

    private int height;
    private int width;
    private String url;

}
