package com.bemonovoid.playqd.core.model.pageable;

import com.bemonovoid.playqd.core.model.SortDirection;
import lombok.Getter;

@Getter
public class FindGenresRequest extends PageableRequest {

    private final String name;

    public FindGenresRequest(int page, int size, SortDirection direction, String name) {
        super(page, size, direction);
        this.name = name;
    }

}
