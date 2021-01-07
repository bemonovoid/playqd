package com.bemonovoid.playqd.library.model.query;

public class AlbumArtQuery implements Query {

    private final String albumLocation;

    public AlbumArtQuery(String albumLocation) {
        this.albumLocation = albumLocation;
    }

    public String getAlbumLocation() {
        return albumLocation;
    }
}
