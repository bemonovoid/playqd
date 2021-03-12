package com.bemonovoid.playqd.core.model.pageable;

import java.util.Optional;

import com.bemonovoid.playqd.core.model.SortDirection;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class PageableRequest {

    private final int page;
    private final int size;
    private final SortDirection direction;

    public SortDirection getDirection() {
        return Optional.ofNullable(direction).orElse(SortDirection.ASC);
    }
}
