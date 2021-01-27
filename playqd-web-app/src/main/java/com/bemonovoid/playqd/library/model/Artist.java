package com.bemonovoid.playqd.library.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Artist {

    private Long id;
    private String name;
    private String country;
}
