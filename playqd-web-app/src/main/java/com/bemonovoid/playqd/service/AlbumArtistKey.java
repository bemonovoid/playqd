package com.bemonovoid.playqd.service;

import java.util.Objects;

/**
 * Allows to correctly scan the albums with the same name for a different artist
 */
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
