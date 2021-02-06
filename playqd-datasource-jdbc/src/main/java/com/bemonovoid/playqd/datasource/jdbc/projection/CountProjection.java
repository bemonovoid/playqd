package com.bemonovoid.playqd.datasource.jdbc.projection;

public interface CountProjection {

    long getArtistId();

    long getAlbumCount();

    long getSongCount();
}
