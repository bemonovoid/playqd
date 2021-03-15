package com.bemonovoid.playqd.core.model.pageable;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import com.bemonovoid.playqd.core.model.SortDirection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindAlbumRequest {

    @PositiveOrZero
    private int page;

    @Positive
    private int size;

    private AlbumsSortBy sortBy = AlbumsSortBy.NAME;

    private SortDirection direction = SortDirection.ASC;

    private Long artistId;
    private String name;
    private String genre;

    public enum AlbumsSortBy {

        NAME,

        DATE

    }
}
