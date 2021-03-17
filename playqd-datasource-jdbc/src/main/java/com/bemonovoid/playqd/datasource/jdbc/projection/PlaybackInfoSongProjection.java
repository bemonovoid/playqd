package com.bemonovoid.playqd.datasource.jdbc.projection;

import java.time.LocalDateTime;

public interface PlaybackInfoSongProjection {

    long getSongId();

    int getPlayCount();

    LocalDateTime getMostRecentPlayDateTime();
}
