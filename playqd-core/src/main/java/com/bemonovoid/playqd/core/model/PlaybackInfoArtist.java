package com.bemonovoid.playqd.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlaybackInfoArtist {

    private final Long artistId;
    private final int playCount;
    private final String lastTimePlayed;
}
