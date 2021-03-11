package com.bemonovoid.playqd.datasource.jdbc.projection;

import java.time.LocalDateTime;

public interface RecentlyPlayedSongProjection {

    long getId();

    LocalDateTime getDatePlayed();

}
