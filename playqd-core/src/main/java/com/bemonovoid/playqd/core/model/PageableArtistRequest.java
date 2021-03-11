package com.bemonovoid.playqd.core.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class PageableArtistRequest {

    private final int page;
    private final int size;

    private final SortBy sortBy;
    private final SortDirection direction;

    public enum SortBy {
        NAME,

        RECENTLY_PLAYED,

        MOST_PLAYED,

        TOTAL_ALBUMS,

        TOTAL_SONGS;
    }
}
