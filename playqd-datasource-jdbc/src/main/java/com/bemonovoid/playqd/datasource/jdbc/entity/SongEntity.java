package com.bemonovoid.playqd.datasource.jdbc.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.bemonovoid.playqd.datasource.jdbc.entity.system.PersistentAuditableEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;

@Table(name = SongEntity.TABLE_NAME, indexes = {
        @Index(name = "SONG_IDX", columnList = SongEntity.COL_NAME + "," + SongEntity.COL_FILE_NAME)
})
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

    public static final String COL_ARTIST_ID = "ARTIST_ID";
    public static final String COL_ALBUM_ID = "ALBUM_ID";

    public static final String COL_PLAY_COUNT = "PLAY_COUNT";
    public static final String COL_FAVORITE = "FAVORITE";
    public static final String COL_SHOW_FILE_NAME_AS_SONG_NAME = "SHOW_FILE_NAME_AS_SONG_NAME";

    private static final String ONE_TO_MANY_MAPPED_BY = "song";

    @Column(name = COL_NAME, length = 1000)
    private String name;

    @Column(name = COL_TRACK_ID)
    private Integer trackId;

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

    @Column(name = COL_FILE_LOCATION)
    private String fileLocation;

    @Column(name = COL_FILE_EXTENSION)
    private String fileExtension;

    @Column(name = COL_COMMENT, length = 3000)
    private String comment;

    @Column(name = COL_LYRICS)
    @Type(type="org.hibernate.type.TextType")
    private String lyrics;

    @Column(name = COL_PLAY_COUNT)
    private int playCount;

    @Column(name = COL_FAVORITE)
    private boolean favorite;

    @ManyToOne(fetch = FetchType.LAZY)
    private ArtistEntity artist;

    @ManyToOne(fetch = FetchType.LAZY)
    private AlbumEntity album;

    @OneToMany(mappedBy = "song")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private List<SongPreferencesEntity> preferences;

}
