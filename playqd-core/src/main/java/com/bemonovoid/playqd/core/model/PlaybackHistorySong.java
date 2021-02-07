package com.bemonovoid.playqd.core.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlaybackHistorySong {

    private final Long songId;
    private final int playCount;
    @JsonFormat(pattern = "dd-MM-YYYY hh:mm")
    private final LocalDateTime lastTimePlayed;
}
