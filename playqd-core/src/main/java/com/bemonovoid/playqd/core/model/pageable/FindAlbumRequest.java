package com.bemonovoid.playqd.core.model.pageable;

import com.bemonovoid.playqd.core.model.SortDirection;
import lombok.Getter;

@Getter
public class FindAlbumRequest extends PageableRequest {

    private final AlbumsSortBy sortBy;
    private final Long artistId;
    private final String name;
    private final String genre;

    public FindAlbumRequest(int page,
                            int size,
                            SortDirection direction,
                            AlbumsSortBy sortBy,
                            Long artistId,
                            String name,
                            String genre) {
        super(page, size, direction);
        this.sortBy = sortBy;
        this.artistId = artistId;
        this.name = name;
        this.genre = genre;
    }

    public enum AlbumsSortBy {

        NAME,

        DATE

    }
}
