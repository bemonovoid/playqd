package com.bemonovoid.playqd.core.model.pageable;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import com.bemonovoid.playqd.core.model.SortDirection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindSongsRequest {

    @PositiveOrZero
    private int page;

    @Positive
    private int size;

    private SortDirection direction = SortDirection.ASC;
    private SongSortBy sortBy = SongSortBy.NAME;

    private String name;

    public enum SongSortBy {

        NAME,

        FAVORITES,

        MOST_PLAYED,

        RECENTLY_ADDED,

        RECENTLY_PLAYED;
    }
}
