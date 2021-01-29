package com.bemonovoid.playqd.core.model;

import java.util.List;

import lombok.Getter;

@Getter
public class Genres {

    private final List<String> genres;
    private final int size;

    public Genres(List<String> genres) {
        this.genres = genres;
        this.size = genres.size();
    }
}
