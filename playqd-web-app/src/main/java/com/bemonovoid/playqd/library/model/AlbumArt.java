package com.bemonovoid.playqd.library.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AlbumArt {

    private final List<String> imageLocations;

    @JsonCreator
    public AlbumArt(@JsonProperty("imageLocations") List<String> imageLocations) {
        this.imageLocations = imageLocations;
    }

    public List<String> getImageLocations() {
        return imageLocations;
    }
}
