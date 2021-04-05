package com.bemonovoid.playqd.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Artist {

    private String id;
    private String spotifyId;
    private String name;
    private String country;
    private long albumCount;
    private long songCount;

}
