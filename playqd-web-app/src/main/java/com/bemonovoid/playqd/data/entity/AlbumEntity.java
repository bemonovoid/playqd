package com.bemonovoid.playqd.data.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Table(name = "Album")
@Entity
public class AlbumEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String date;

    private String genre;

    @ElementCollection
    private List<String> artLocations;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private ArtistEntity artist;

    @OneToMany(mappedBy = "album", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<SongEntity> songs;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGenre() {
        return genre;
    }

    public String getDate() {
        return date;
    }

    public ArtistEntity getArtist() {
        return artist;
    }

    public List<String> getArtLocations() {
        return artLocations;
    }

    public List<SongEntity> getSongs() {
        return songs;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setArtist(ArtistEntity artist) {
        this.artist = artist;
    }

    public void setArtLocations(List<String> artLocations) {
        this.artLocations = artLocations;
    }

    public void setSongs(List<SongEntity> songs) {
        this.songs = songs;
    }

    public void addSong(SongEntity songEntity) {
        if (songs == null) {
            songs = new ArrayList<>();
        }
        songEntity.setArtist(getArtist());
        songEntity.setAlbum(this);
        getSongs().add(songEntity);
    }
}
