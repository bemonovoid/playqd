package com.bemonovoid.playqd.data.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Table(name = ArtistEntity.TABLE_NAME)
@Entity
public class ArtistEntity {

    public static final String TABLE_NAME = "ARTIST";

    public static final String COL_PK_ID = "ID";
    public static final String COL_NAME = "NAME";
    public static final String COL_SIMPLE_NAME = "SIMPLE_NAME";

    @Id
    @Column(name = COL_PK_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = COL_NAME)
    private String name;

    @Column(name = COL_SIMPLE_NAME)
    private String simpleName;

    @OneToMany(mappedBy = "artist", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<AlbumEntity> albums;

    public Long getId() {
        return id;
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

    public void addAlbum(AlbumEntity albumEntity) {
        if (albums == null) {
            albums = new ArrayList<>();
        }
        albumEntity.setArtist(this);
        getAlbums().add(albumEntity);
    }

}
