package com.bemonovoid.playqd.core.model.pageable;

import com.bemonovoid.playqd.core.model.SortDirection;
import lombok.Getter;

@Getter
public class FindArtistsRequest extends PageableRequest {

    private final ArtistSortBy sortBy;
    private final String name;

    public FindArtistsRequest(int page, int size, SortDirection direction, ArtistSortBy sortBy, String name) {
        super(page, size, direction);
        this.name = name;
        this.sortBy = sortBy;
    }

    public enum ArtistSortBy {

        NAME,

        RECENTLY_PLAYED,

        MOST_PLAYED,

        TOTAL_ALBUMS,

        TOTAL_SONGS;
    }
}
