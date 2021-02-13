package com.bemonovoid.playqd.core.model.query;

import java.util.Objects;

public class AlbumSongsQuery {

    private final long albumId;

    public AlbumSongsQuery(long albumId) {
        this.albumId = albumId;
    }

    public long getAlbumId() {
        return albumId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlbumSongsQuery that = (AlbumSongsQuery) o;
        return Objects.equals(albumId, that.albumId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(albumId);
    }
}
