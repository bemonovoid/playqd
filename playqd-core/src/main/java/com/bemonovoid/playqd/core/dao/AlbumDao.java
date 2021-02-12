package com.bemonovoid.playqd.core.dao;

import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.model.Album;

public interface AlbumDao {

    Optional<Album> getOne(long id);

    List<Album> getAll();

    List<String> getGenres();

    List<Album> getAllByGenre(String genre);

    List<Album> getArtistAlbums(long artistId);

    Album save(Album albumEntity);

    void updateArtwork(Long albumId, String mbReleaseId, byte[] binary);

    void updateAlbum(Album album);
}
