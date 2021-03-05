package com.bemonovoid.playqd.core.dao;

import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.AlbumPreferences;
import com.bemonovoid.playqd.core.model.MoveResult;

public interface AlbumDao {

    Optional<Album> findOne(long id);

    Album getOne(long id);

    List<Album> getAll();

    List<String> getGenres();

    List<Album> getGenreAlbums(String genre);

    List<Album> getArtistAlbums(long artistId);

    Album save(Album albumEntity);

    void updateAlbum(Album album);

    void updateAlbumPreferences(AlbumPreferences preferences);

    void saveAlbumImage(long albumId, byte[] binaryData);

    MoveResult move(long albumIdFrom, Long albumIdTo);
}
