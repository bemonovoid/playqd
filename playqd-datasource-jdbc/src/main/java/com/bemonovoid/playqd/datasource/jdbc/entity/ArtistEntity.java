package com.bemonovoid.playqd.datasource.jdbc.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.bemonovoid.playqd.datasource.jdbc.entity.system.PersistentAuditableEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Table(name = ArtistEntity.TABLE_NAME, indexes = {
        @Index(name = "ARTIST_IDX", columnList = ArtistEntity.COL_NAME + "," + ArtistEntity.COL_SIMPLE_NAME)
})
@Entity
@Getter
@Setter
public class ArtistEntity extends PersistentAuditableEntity {

    public static final String TABLE_NAME = "ARTIST";

    public static final String COL_PK_ID = "ID";
    public static final String COL_NAME = "NAME";
    public static final String COL_SIMPLE_NAME = "SIMPLE_NAME";
    public static final String COL_COUNTRY = "COUNTRY";
    public static final String COL_SPOTIFY_ARTIST_ID = "SPOTIFY_ARTIST_ID";
    public static final String COL_SPOTIFY_ARTIST_NAME = "SPOTIFY_ARTIST_NAME";

    public static final String FLD_NAME = "name";

    @Column(name = COL_NAME)
    private String name;

    @Column(name = COL_SIMPLE_NAME)
    private String simpleName;

    @Column(name = COL_COUNTRY)
    private String country;

    @Column(name = COL_SPOTIFY_ARTIST_ID)
    private String spotifyArtistId;

    @Column(name = COL_SPOTIFY_ARTIST_NAME)
    private String spotifyArtistName;

    @OneToMany(mappedBy = "artist")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private List<AlbumEntity> albums;
}
