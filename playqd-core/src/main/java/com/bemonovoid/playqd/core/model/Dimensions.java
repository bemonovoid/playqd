package com.bemonovoid.playqd.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class Dimensions implements Comparable<Dimensions> {

    private final int height;
    private final int width;

    @JsonCreator
    public Dimensions(@JsonProperty("height") int height, @JsonProperty("width") int width) {
        this.height = height;
        this.width = width;
    }

    @Override
    public int compareTo(Dimensions that) {
        return Integer.compare(this.getHeight(), that.getHeight());
    }
}
