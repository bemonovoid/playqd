package com.bemonovoid.playqd.data.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.domain.Persistable;

@Table(name = SongEntity.TABLE_NAME)
@Entity
public class SongEntity implements Persistable<Long> {

    public static final String TABLE_NAME = "SONG";

    public static final String COL_PK_ID = "ID";
    public static final String COL_NAME = "NAME";
    public static final String COL_TRACK_ID = "TRACK_ID";
    public static final String COL_DURATION = "DURATION";
    public static final String COL_COMMENT = "COMMENT";
    public static final String COL_FILE_NAME = "FILE_NAME";
    public static final String COL_FILE_EXTENSION = "FILE_EXTENSION";
    public static final String COL_FILE_LOCATION = "FILE_LOCATION";
    public static final String COL_AUDIO_SAMPLE_RATE = "AUDIO_SAMPLE_RATE";
    public static final String COL_AUDIO_ENCODING_TYPE = "AUDIO_ENCODING_TYPE";
    public static final String COL_AUDIO_CHANNEL_TYPE = "AUDIO_CHANNEL_TYPE";
    public static final String COL_AUDIO_BIT_RATE = "AUDIO_BIT_RATE";

    public static final String COL_MB_TRACK_ID = "MB_TRACK_ID";

    public static final String COL_ARTIST_ID = "ARTIST_ID";
    public static final String COL_ALBUM_ID = "ALBUM_ID";

    @Id
    @Column(name = COL_PK_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = COL_NAME, length = 1000)
    private String name;

    @Column(name = COL_TRACK_ID)
    private String trackId;

    @Column(name = COL_DURATION)
    private int duration;

    @Column(name = COL_AUDIO_CHANNEL_TYPE)
    private String audioChannelType;

    @Column(name = COL_AUDIO_SAMPLE_RATE)
    private String audioSampleRate;

    @Column(name = COL_AUDIO_BIT_RATE)
    private String audioBitRate;

    @Column(name = COL_AUDIO_ENCODING_TYPE)
    private String audioEncodingType;

    @Column(name = COL_FILE_NAME)
    private String fileName;

    @Column(name = COL_FILE_LOCATION, length = 8000)
    private String fileLocation;

    @Column(name = COL_FILE_EXTENSION)
    private String fileExtension;

    @Column(name = COL_COMMENT, length = 8000)
    private String comment;

    @Column(name = COL_MB_TRACK_ID)
    private String mbTrackId;

    @ManyToOne(fetch = FetchType.LAZY)
    private ArtistEntity artist;

    @ManyToOne(fetch = FetchType.LAZY)
    private AlbumEntity album;

    @OneToMany(mappedBy = "song")
    private List<PlaybackHistoryEntity> playBackHistory;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return getId() != null;
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

    public String getMbTrackId() {
        return mbTrackId;
    }

    public void setMbTrackId(String mbTrackId) {
        this.mbTrackId = mbTrackId;
    }
}
