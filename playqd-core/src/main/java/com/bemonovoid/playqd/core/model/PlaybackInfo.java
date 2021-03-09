package com.bemonovoid.playqd.core.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaybackInfo {

    private final long songId;
    private final int playCount;
    private final boolean favorite;
    @JsonFormat(pattern = "dd-MM-YYYY hh:mm")
    private final LocalDateTime lastPlayedTime;
}
