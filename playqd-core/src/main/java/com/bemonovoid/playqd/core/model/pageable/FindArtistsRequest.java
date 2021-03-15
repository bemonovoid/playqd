package com.bemonovoid.playqd.core.model.pageable;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import com.bemonovoid.playqd.core.model.SortDirection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindArtistsRequest {

    @PositiveOrZero
    private int page;

    @Positive
    private int size;

    private SortDirection direction = SortDirection.ASC;
    private FindArtistsRequest.ArtistSortBy sortBy = FindArtistsRequest.ArtistSortBy.NAME;

    private String name;

    public enum ArtistSortBy {

        NAME,

        RECENTLY_PLAYED,

        MOST_PLAYED,

        TOTAL_ALBUMS,

        TOTAL_SONGS;
    }
}
