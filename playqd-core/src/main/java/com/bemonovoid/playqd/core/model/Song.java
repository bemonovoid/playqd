package com.bemonovoid.playqd.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Song {

    private Long id;
    private String name;
    private String originalName;
    private int duration;
    private String comment;
    private int trackId;
    private String originalTrackId;
    private String lyrics;

    private String audioBitRate;
    private String audioChannelType;
    private String audioEncodingType;
    private String audioSampleRate;

    @JsonIgnore
    private String fileLocation;
    private String fileExtension;
    private String fileName;

    private Artist artist;
    private Album album;
    private PlaybackInfo playbackInfo;
    private SongPreferences preferences;

}
