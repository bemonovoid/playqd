package com.bemonovoid.playqd.datasource.jdbc.projection;

import java.util.UUID;

public interface CountProjection {

    UUID getArtistId();

    long getAlbumCount();

    long getSongCount();
}
