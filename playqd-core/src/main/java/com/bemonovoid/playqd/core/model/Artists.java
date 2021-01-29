package com.bemonovoid.playqd.core.model;

import java.util.List;

import lombok.Getter;

@Getter
public class Artists {

    private final List<Artist> artists;
    private final int size;

    public Artists(List<Artist> artists) {
        this.artists = artists;
        this.size = artists.size();
    }
}
