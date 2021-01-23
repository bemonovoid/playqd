package com.bemonovoid.playqd.data.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.springframework.data.domain.Persistable;

@Table(name = AlbumEntity.TABLE_NAME)
@Entity
public class AlbumEntity implements Persistable<Long> {

    public static final String TABLE_NAME = "ALBUM";

    public static final String COL_PK_ID = "ID";
    public static final String COL_NAME = "NAME";
    public static final String COL_SIMPLE_NAME = "SIMPLE_NAME";
    public static final String COL_DATE = "DATE";
    public static final String COL_GENRE = "GENRE";
    public static final String COL_ARTIST_ID = "ARTIST_ID";
    public static final String COL_ARTWORK_STATUS = "ARTWORK_STATUS";
    public static final String COL_ARTWORK_BINARY = "ARTWORK_BINARY";

    public static final String COL_MB_RELEASE_ID = "MB_RELEASE_ID";

    @Id
    @Column(name = COL_PK_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = COL_MB_RELEASE_ID)
    private String mbReleaseId;

    @Column(name = COL_NAME)
    private String name;

    @Column(name = COL_SIMPLE_NAME)
    private String simpleName;

    @Column(name = COL_DATE)
    private String date;

    @Column(name = COL_GENRE)
    private String genre;

    @Column(name = COL_ARTWORK_STATUS)
    @Enumerated(EnumType.STRING)
    private ArtworkStatus artworkStatus;

    @Lob
    @Column(name = COL_ARTWORK_BINARY)
    @Type(type="org.hibernate.type.ImageType")
    private byte[] artworkBinary;

    @ManyToOne(fetch = FetchType.LAZY)
    private ArtistEntity artist;

    @OneToMany(mappedBy = "album")
    private List<SongEntity> songs;

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

    public String getMbReleaseId() {
        return mbReleaseId;
    }

    public void setMbReleaseId(String mbReleaseId) {
        this.mbReleaseId = mbReleaseId;
    }

    public ArtworkStatus getArtworkStatus() {
        return artworkStatus;
    }

    public void setArtworkStatus(ArtworkStatus artworkStatus) {
        this.artworkStatus = artworkStatus;
    }

    public byte[] getArtworkBinary() {
        return artworkBinary;
    }

    public void setArtworkBinary(byte[] artworkBinary) {
        this.artworkBinary = artworkBinary;
    }
}
