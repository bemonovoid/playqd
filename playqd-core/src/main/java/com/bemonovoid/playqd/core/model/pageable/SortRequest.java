package com.bemonovoid.playqd.core.model.pageable;

import com.bemonovoid.playqd.core.model.SortDirection;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SortRequest {

    private SortBy sortBy;
    private SortDirection direction;
}
