package com.bemonovoid.playqd.datasource.jdbc.projection;

import java.time.LocalDateTime;

public interface PlaybackHistorySongProjection {

    long getSongId();

    int getPlayCount();

    LocalDateTime getMostRecentPlayDateTime();
}
