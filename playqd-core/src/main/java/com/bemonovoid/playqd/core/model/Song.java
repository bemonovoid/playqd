package com.bemonovoid.playqd.core.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Song {

    private String id;
    private String name;
    private String originalName;
    private int duration;
    private String comment;
    private String trackId;
    private String lyrics;

    private String audioBitRate;
    private String audioChannelType;
    private String audioEncodingType;
    private String audioSampleRate;

    @JsonIgnore
    private String fileLocation;
    private String fileExtension;
    private String fileName;

    private int playCount;
    private boolean favorite;

    @JsonFormat(pattern = "dd-MM-YYYY hh:mm")
    private LocalDateTime lastPlayedTime;

    private Artist artist;
    private Album album;

}
