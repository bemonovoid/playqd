package com.bemonovoid.playqd.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Artist {

    private Long id;
    private String name;
    private String country;
    private long albumCount;
    private long songCount;

    @JsonIgnore
    private String simpleName;

    @JsonIgnore
    private String mbArtistId;

    private PlaybackHistoryArtist playbackHistory;
}
