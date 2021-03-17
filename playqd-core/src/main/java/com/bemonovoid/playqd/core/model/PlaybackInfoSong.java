package com.bemonovoid.playqd.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlaybackInfoSong {

    private final Long songId;
    private final int playCount;
    private final String lastTimePlayed;
}
