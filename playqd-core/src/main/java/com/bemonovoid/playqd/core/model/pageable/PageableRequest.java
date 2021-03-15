package com.bemonovoid.playqd.core.model.pageable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageableRequest {

    private final int page;
    private final int size;
    private final SortRequest sort;
}
