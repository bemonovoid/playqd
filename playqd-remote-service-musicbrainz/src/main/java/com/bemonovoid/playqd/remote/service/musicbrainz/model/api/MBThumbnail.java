package com.bemonovoid.playqd.remote.service.musicbrainz.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBThumbnail {

    private String small;

    private String large;

    @JsonProperty("250")
    private String twoHundredAndFifty;

    @JsonProperty("500")
    private String fiveHundred;
}
