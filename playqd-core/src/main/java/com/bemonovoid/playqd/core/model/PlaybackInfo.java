package com.bemonovoid.playqd.core.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlaybackInfo {

    private final Long songId;
    private final int playCount;
    private final boolean favorite;
    @JsonFormat(pattern = "dd-MM-YYYY hh:mm")
    private final LocalDateTime lastPlayedTime;
}
