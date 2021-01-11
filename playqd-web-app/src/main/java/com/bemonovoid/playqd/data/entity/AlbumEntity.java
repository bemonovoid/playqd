package com.bemonovoid.playqd.data.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Table(name = AlbumEntity.TABLE_NAME)
@Entity
public class AlbumEntity {

    public static final String TABLE_NAME = "ALBUM";

    public static final String COL_PK_ID = "ID";
    public static final String COL_NAME = "NAME";
    public static final String COL_SIMPLE_NAME = "SIMPLE_NAME";
    public static final String COL_DATE = "DATE";
    public static final String COL_GENRE = "GENRE";
    public static final String COL_ARTIST_ID = "ARTIST_ID";

    @Id
    @Column(name = COL_PK_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = COL_NAME)
    private String name;

    @Column(name = COL_SIMPLE_NAME)
    private String simpleName;

    @Column(name = COL_DATE)
    private String date;

    @Column(name = COL_GENRE)
    private String genre;

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

    public void setSongs(List<SongEntity> songs) {
        this.songs = songs;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
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
