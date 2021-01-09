package com.bemonovoid.playqd.library.service.impl;

import java.util.Objects;

class AlbumArtistKey {

    private final long artistId;
    private final String albumLowerName;

    AlbumArtistKey(long artistId, String albumLowerName) {
        this.artistId = artistId;
        this.albumLowerName = albumLowerName;
    }

    public long getArtistId() {
        return artistId;
    }

    public String getAlbumLoweCaseName() {
        return albumLowerName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlbumArtistKey that = (AlbumArtistKey) o;
        return artistId == that.artistId && Objects.equals(albumLowerName, that.albumLowerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artistId, albumLowerName);
    }
}
