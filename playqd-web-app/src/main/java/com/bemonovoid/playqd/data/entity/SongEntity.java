package com.bemonovoid.playqd.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table(name = "Song")
@Entity
public class SongEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 1000)
    private String name;

    private String trackId;

    private int duration;

    private String audioChannelType;

    private String audioSampleRate;

    private String audioBitRate;

    private String audioEncodingType;

    private String fileName;

    @Column(length = 8000)
    private String fileLocation;

    private String fileExtension;

    @Column(length = 8000)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    private ArtistEntity artist;

    @ManyToOne(fetch = FetchType.LAZY)
    private AlbumEntity album;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTrackId() {
        return trackId;
    }

    public int getDuration() {
        return duration;
    }

    public String getAudioChannelType() {
        return audioChannelType;
    }

    public String getAudioSampleRate() {
        return audioSampleRate;
    }

    public String getAudioEncodingType() {
        return audioEncodingType;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public ArtistEntity getArtist() {
        return artist;
    }

    public AlbumEntity getAlbum() {
        return album;
    }

    public String getComment() {
        return comment;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setAudioChannelType(String audioChannelType) {
        this.audioChannelType = audioChannelType;
    }

    public void setAudioSampleRate(String audioSampleRate) {
        this.audioSampleRate = audioSampleRate;
    }

    public void setAudioEncodingType(String audioEncodingType) {
        this.audioEncodingType = audioEncodingType;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public void setArtist(ArtistEntity artist) {
        this.artist = artist;
    }

    public void setAlbum(AlbumEntity album) {
        this.album = album;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAudioBitRate() {
        return audioBitRate;
    }

    public void setAudioBitRate(String audioBitRate) {
        this.audioBitRate = audioBitRate;
    }
}
