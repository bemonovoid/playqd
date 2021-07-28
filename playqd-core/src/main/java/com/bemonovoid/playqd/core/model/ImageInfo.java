package com.bemonovoid.playqd.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class ImageInfo {

    private final String url;
    private final Dimensions dimensions;

    @JsonCreator
    public ImageInfo(@JsonProperty("url") String url, @JsonProperty("dimensions") Dimensions dimensions) {
        this.url = url;
        this.dimensions = dimensions;
    }
}
