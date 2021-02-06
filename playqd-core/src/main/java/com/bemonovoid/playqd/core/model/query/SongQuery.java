package com.bemonovoid.playqd.core.model.query;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SongQuery {

    private int pageSize;
    private SongFilter filter;

}
