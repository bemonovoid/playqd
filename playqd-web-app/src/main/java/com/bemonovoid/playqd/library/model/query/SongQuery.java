package com.bemonovoid.playqd.library.model.query;

public class SongQuery {

    private final long songId;

    public SongQuery(long songId) {
        this.songId = songId;
    }

    public long getSongId() {
        return songId;
    }
}
