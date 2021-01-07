package com.bemonovoid.playqd.library.model;

import java.util.List;

public class ArtistAlbums {

    private final Artist artist;
    private final List<Album> albums;

    public ArtistAlbums(Artist artist, List<Album> albums) {
        this.artist = artist;
        this.albums = albums;
    }

    public Artist getArtist() {
        return artist;
    }

    public List<Album> getAlbums() {
        return albums;
    }
}
