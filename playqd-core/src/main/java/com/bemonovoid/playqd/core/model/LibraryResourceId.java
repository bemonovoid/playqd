package com.bemonovoid.playqd.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LibraryResourceId {

    private final long id;
    private ResourceTarget target;
    private final String authToken;

}
