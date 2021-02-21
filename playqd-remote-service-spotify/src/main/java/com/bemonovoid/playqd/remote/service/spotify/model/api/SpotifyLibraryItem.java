package com.bemonovoid.playqd.remote.service.spotify.model.api;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
public class SpotifyLibraryItem {

    private String id;
    private String name;
    private List<SpotifyImage> images;
}
