package com.bemonovoid.playqd.data.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.bemonovoid.playqd.data.entity.AlbumEntity;

@Table(name = "Artist")
@Entity
public class ArtistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

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

    public void addAlbum(AlbumEntity albumEntity) {
        if (albums == null) {
            albums = new ArrayList<>();
        }
        albumEntity.setArtist(this);
        getAlbums().add(albumEntity);
    }

}
