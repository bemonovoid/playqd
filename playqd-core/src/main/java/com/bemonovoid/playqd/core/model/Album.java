package com.bemonovoid.playqd.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Album {

    private Long id;
    private String name;
    private String genre;
    private String date;
    private Integer totalTimeInSeconds;
    private String totalTimeHumanReadable;
    private Artist artist;

    @JsonIgnore
    private Artwork artwork;

    @JsonIgnore
    private String simpleName;

    @JsonIgnore
    private String mbReleaseId;

    @JsonIgnore
    private boolean overrideSongNameWithFileName;
}
