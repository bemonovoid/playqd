package com.bemonovoid.playqd.datasource.jdbc.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.bemonovoid.playqd.datasource.jdbc.entity.system.PersistentAuditableEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;

@Getter
@Setter
@Entity
@Table(name = AlbumEntity.TABLE_NAME, indexes = {
        @Index(name = "ALBUM_IDX", columnList = AlbumEntity.COL_NAME + "," + AlbumEntity.COL_SIMPLE_NAME)
})
public class AlbumEntity extends PersistentAuditableEntity {

    public static final String TABLE_NAME = "ALBUM";

    public static final String COL_NAME = "NAME";
    public static final String COL_SIMPLE_NAME = "SIMPLE_NAME";
    public static final String COL_DATE = "DATE";
    public static final String COL_GENRE = "GENRE";
    public static final String COL_TOTAL_TIME_IN_SECONDS = "TOTAL_TIME_IN_SECONDS";
    public static final String COL_ARTIST_ID = "ARTIST_ID";
    public static final String COL_IMAGE = "IMAGE";

    public static final String FLD_NAME = "name";
    public static final String FLD_DATE = "date";

    @Column(name = COL_NAME)
    private String name;

    @Column(name = COL_SIMPLE_NAME)
    private String simpleName;

    @Column(name = COL_DATE)
    private String date;

    @Column(name = COL_GENRE)
    private String genre;

    @Formula("(select SUM(s.DURATION) from SONG s where s.ALBUM_ID = ID)")
    private Integer totalTimeInSeconds;

    @Lob
    @Column(name = COL_IMAGE)
    @Type(type="org.hibernate.type.ImageType")
    private byte[] image;

    @ManyToOne(fetch = FetchType.LAZY)
    private ArtistEntity artist;

    @OneToMany(mappedBy = "album")
    private List<SongEntity> songs;
}
