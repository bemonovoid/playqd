package com.bemonovoid.playqd.library.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Song {

    private long id;
    private String name;
    private int trackId;
    private int duration;
    private String comment;

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
}
