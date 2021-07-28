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
        @Index(name = "ARTIST_IDX", columnList = ArtistEntity.COL_NAME)
})
@Entity
@Getter
@Setter
public class ArtistEntity extends PersistentAuditableEntity {

    public static final String TABLE_NAME = "ARTIST";

    public static final String COL_PK_ID = "ID";
    public static final String COL_NAME = "NAME";
    public static final String COL_COUNTRY = "COUNTRY";
    public static final String COL_SPOTIFY_ARTIST_ID = "SPOTIFY_ARTIST_ID";

    @Column(name = COL_NAME)
    private String name;

    @Column(name = COL_COUNTRY)
    private String country;

    @Column(name = COL_SPOTIFY_ARTIST_ID)
    private String spotifyArtistId;

    @OneToMany(mappedBy = "artist")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private List<AlbumEntity> albums;

    @OneToMany(mappedBy = "artist")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private List<ArtistImageEntity> images;
}
