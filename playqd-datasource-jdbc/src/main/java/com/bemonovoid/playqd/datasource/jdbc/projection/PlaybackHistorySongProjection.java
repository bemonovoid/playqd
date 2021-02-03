package com.bemonovoid.playqd.datasource.jdbc.projection;

import java.time.LocalDateTime;

public interface PlaybackHistorySongProjection {

    long getArtistId();

    long getAlbumId();

    long getSongId();

    int getPlayCount();

    LocalDateTime getMostRecentPlayDateTime();
}
