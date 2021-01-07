package com.bemonovoid.playqd.library.model.query;

import java.util.Objects;

public class ArtistAlbumsQuery implements Query {

    private final long artistId;

    public ArtistAlbumsQuery(long artistId) {
        this.artistId = artistId;
    }

    public long getArtistId() {
        return artistId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtistAlbumsQuery that = (ArtistAlbumsQuery) o;
        return Objects.equals(artistId, that.artistId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artistId);
    }
}
