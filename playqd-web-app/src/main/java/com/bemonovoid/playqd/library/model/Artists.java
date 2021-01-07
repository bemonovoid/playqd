package com.bemonovoid.playqd.library.model;

import java.util.List;

public class Artists {

    private final List<Artist> artists;

    public Artists(List<Artist> artists) {
        this.artists = artists;
    }

    public List<Artist> getArtists() {
        return artists;
    }
}
