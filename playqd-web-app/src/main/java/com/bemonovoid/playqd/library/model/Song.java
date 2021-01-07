package com.bemonovoid.playqd.library.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.bemonovoid.playqd.library.model.Album;
import com.bemonovoid.playqd.library.model.Artist;

public class Song {

    private long id;
    private String name;
    private String audioBitRate;
    private String audioEncodingType;
    private String audioChannelType;
    private String audioSampleRate;
    private String fileExtension;
    private int trackId;
    private int duration;
    private String comment;
    private Artist artist;
    private Album album;

    @JsonIgnore
    private String fileLocation;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAudioBitRate() {
        return audioBitRate;
    }

    public void setAudioBitRate(String audioBitRate) {
        this.audioBitRate = audioBitRate;
    }

    public String getAudioEncodingType() {
        return audioEncodingType;
    }

    public void setAudioEncodingType(String audioEncodingType) {
        this.audioEncodingType = audioEncodingType;
    }

    public String getAudioChannelType() {
        return audioChannelType;
    }

    public void setAudioChannelType(String audioChannelType) {
        this.audioChannelType = audioChannelType;
    }

    public String getAudioSampleRate() {
        return audioSampleRate;
    }

    public void setAudioSampleRate(String audioSampleRate) {
        this.audioSampleRate = audioSampleRate;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }
}
