package com.bemonovoid.playqd.core.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Artist {

    private String id;

    @JsonProperty("spotify_id")
    private String spotifyId;

    private String name;
    private String country;

    @JsonProperty("album_count")
    private long albumCount;

    @JsonProperty("song_count")
    private long songCount;

    private List<ImageInfo> images;

}
