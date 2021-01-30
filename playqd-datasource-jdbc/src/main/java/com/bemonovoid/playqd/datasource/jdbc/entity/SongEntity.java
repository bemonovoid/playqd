package com.bemonovoid.playqd.datasource.jdbc.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Table(name = SongEntity.TABLE_NAME)
@Entity
@Getter
@Setter
public class SongEntity extends PersistentAuditableEntity<Long> {

    public static final String TABLE_NAME = "SONG";

    public static final String COL_NAME = "NAME";
    public static final String COL_TRACK_ID = "TRACK_ID";
    public static final String COL_DURATION = "DURATION";
    public static final String COL_COMMENT = "COMMENT";
    public static final String COL_LYRICS = "LYRICS";
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

    @Column(name = COL_LYRICS, length = 8000)
    private String lyrics;

    @Column(name = COL_MB_TRACK_ID)
    private String mbTrackId;

    @ManyToOne(fetch = FetchType.LAZY)
    private ArtistEntity artist;

    @ManyToOne(fetch = FetchType.LAZY)
    private AlbumEntity album;

    @OneToMany(mappedBy = "song")
    private List<PlaybackHistoryEntity> playBackHistory;

}
