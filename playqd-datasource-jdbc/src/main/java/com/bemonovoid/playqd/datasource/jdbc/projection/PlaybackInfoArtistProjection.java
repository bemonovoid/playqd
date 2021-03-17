package com.bemonovoid.playqd.datasource.jdbc.projection;

import java.time.LocalDateTime;

public interface PlaybackInfoArtistProjection {

    Long getArtistId();

    int getPlayCount();

    LocalDateTime getMostRecentPlayDateTime();
}
