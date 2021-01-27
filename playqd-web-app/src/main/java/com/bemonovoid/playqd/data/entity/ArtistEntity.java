package com.bemonovoid.playqd.data.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.domain.Persistable;

@Table(name = ArtistEntity.TABLE_NAME)
@Entity
public class ArtistEntity implements Persistable<Long> {

    public static final String TABLE_NAME = "ARTIST";

    public static final String COL_PK_ID = "ID";
    public static final String COL_NAME = "NAME";
    public static final String COL_SIMPLE_NAME = "SIMPLE_NAME";
    public static final String COL_MB_ARTIST_ID = "MB_ARTIST_ID";
    public static final String COL_COUNTRY = "COUNTRY";

    @Id
    @Column(name = COL_PK_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = COL_MB_ARTIST_ID)
    private String mbArtistId;

    @Column(name = COL_NAME)
    private String name;

    @Column(name = COL_SIMPLE_NAME)
    private String simpleName;

    @OneToMany(mappedBy = "artist")
    private List<AlbumEntity> albums;

    @Column(name = COL_COUNTRY)
    private String country;

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

    public List<AlbumEntity> getAlbums() {
        return albums;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAlbums(List<AlbumEntity> albums) {
        this.albums = albums;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public String getMbArtistId() {
        return mbArtistId;
    }

    public void setMbArtistId(String mbArtistId) {
        this.mbArtistId = mbArtistId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
