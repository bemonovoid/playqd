package com.bemonovoid.playqd.core.model.pageable;

import com.bemonovoid.playqd.core.model.SortDirection;
import lombok.Getter;

@Getter
public class FindSongsRequest extends PageableRequest {

    private final Long albumId;
    private final String name;
    private final SongSortBy sortBy;

    public FindSongsRequest(int page, int size, SortDirection direction, Long albumId, String name, SongSortBy sortBy) {
        super(page, size, direction);
        this.albumId = albumId;
        this.name = name;
        this.sortBy = sortBy;
    }

    public enum SongSortBy {

        NAME,

        FAVORITES,

        MOST_PLAYED,

        RECENTLY_ADDED,

        RECENTLY_PLAYED;
    }
}
