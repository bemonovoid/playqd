package com.bemonovoid.playqd.core.model.pageable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import com.bemonovoid.playqd.core.model.SortDirection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindGenresRequest {

    @PositiveOrZero
    private int page;

    @Positive
    private int size;

    private String name;

    @NotNull
    private SortDirection direction = SortDirection.ASC;

}
