package com.bemonovoid.playqd.core.dao;

import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.model.Album;

public interface AlbumDao {

    Optional<Album> findOne(long id);

    Album getOne(long id);

    List<Album> getAll();

    List<String> getGenres();

    List<Album> getGenreAlbums(String genre);

    List<Album> getArtistAlbums(long artistId);

    Album save(Album albumEntity);

    void updateAlbum(Album album);

    void setArtworkBinary(long albumId, byte[] binaryData);

    void move(long albumIdFrom, Long albumIdTo);
}
