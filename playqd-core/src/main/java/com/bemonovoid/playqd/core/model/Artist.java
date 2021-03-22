package com.bemonovoid.playqd.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Artist {

    private String id;
    private String name;
    private String country;
    private long albumCount;
    private long songCount;

    @JsonIgnore
    private String simpleName;

    @JsonIgnore
    private String spotifyId;

    @JsonIgnore
    private String spotifyName;

}
