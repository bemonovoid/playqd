package com.bemonovoid.playqd.library.model.query;

public class ArtworkQuery {

    private final Long albumId;
    private final Long songId;

    private ArtworkQuery(Long albumId, Long songId) {
        this.albumId = albumId;
        this.songId = songId;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public Long getSongId() {
        return songId;
    }

    public static ArtworkQuery fromSongId(long songId) {
        return new ArtworkQuery(null, songId);
    }

    public static ArtworkQuery fromAlbumId(long albumId) {
        return new ArtworkQuery(albumId, null);
    }
}
