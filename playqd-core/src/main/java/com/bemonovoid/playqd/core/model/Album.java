package com.bemonovoid.playqd.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Album {

    private String id;
    private String name;
    private String genre;
    private String date;
    private Integer totalTimeInSeconds;
    private Artist artist;

    @JsonIgnore
    private Image image;

    @JsonIgnore
    private String simpleName;

    @JsonIgnore
    private String mbReleaseId;

}
