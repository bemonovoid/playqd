package com.bemonovoid.playqd.library.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AlbumSongs {

    private final Artist artist;
    private final Album album;
    private final List<Song> songs;

    @JsonCreator
    public AlbumSongs(@JsonProperty("artist") Artist artist,
                      @JsonProperty("album") Album album,
                      @JsonProperty("songs") List<Song> songs) {
        this.artist = artist;
        this.album = album;
        this.songs = songs;
    }

    public Artist getArtist() {
        return artist;
    }

    public Album getAlbum() {
        return album;
    }

    public List<Song> getSongs() {
        return songs;
    }
}
