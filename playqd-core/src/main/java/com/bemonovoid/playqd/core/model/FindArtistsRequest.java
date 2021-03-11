package com.bemonovoid.playqd.core.model;

import lombok.Getter;

@Getter
public class FindArtistsRequest extends PageableArtistRequest {

    private String name;

    public FindArtistsRequest(int page, int size, SortBy sortBy, SortDirection direction, String name) {
        super(page, size, sortBy, direction);
        this.name = name;
    }
}
