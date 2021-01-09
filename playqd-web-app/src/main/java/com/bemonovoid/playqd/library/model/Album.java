package com.bemonovoid.playqd.library.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Album {

    private final long id;
    private final String name;
    private final Artist artist;
    private final String genre;
    private final String year;
    private final String totalTime;

    @JsonCreator
    public Album(@JsonProperty("id") long id,
                 @JsonProperty("name") String name,
                 @JsonProperty("genre") String genre,
                 @JsonProperty("year") String year,
                 @JsonProperty("totalTime") String totalTime,
                 @JsonProperty("artist") Artist artist) {
        this.id = id;
        this.name = name;
        this.genre = genre;
        this.year = year;
        this.totalTime = totalTime;
        this.artist = artist;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Artist getArtist() {
        return artist;
    }

    public String getGenre() {
        return genre;
    }

    public String getYear() {
        return year;
    }

    public String getTotalTime() {
        return totalTime;
    }
}
