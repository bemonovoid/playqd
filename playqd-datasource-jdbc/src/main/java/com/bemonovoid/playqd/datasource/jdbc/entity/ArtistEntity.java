package com.bemonovoid.playqd.datasource.jdbc.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.bemonovoid.playqd.datasource.jdbc.entity.system.PersistentAuditableEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Table(name = ArtistEntity.TABLE_NAME)
@Entity
@Getter
@Setter
public class ArtistEntity extends PersistentAuditableEntity<Long> {

    public static final String TABLE_NAME = "ARTIST";

    public static final String COL_PK_ID = "ID";
    public static final String COL_NAME = "NAME";
    public static final String COL_SIMPLE_NAME = "SIMPLE_NAME";
    public static final String COL_SPOTIFY_ARTIST_ID = "SPOTIFY_ARTIST_ID";
    public static final String COL_COUNTRY = "COUNTRY";

    @Id
    @Column(name = COL_PK_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = COL_SPOTIFY_ARTIST_ID)
    private String spotifyArtistId;

    @Column(name = COL_NAME)
    private String name;

    @Column(name = COL_SIMPLE_NAME)
    private String simpleName;

    @OneToMany(mappedBy = "artist")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private List<AlbumEntity> albums;

    @Column(name = COL_COUNTRY)
    private String country;
}
