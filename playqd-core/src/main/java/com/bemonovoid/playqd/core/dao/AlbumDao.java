package com.bemonovoid.playqd.core.dao;

import java.util.Optional;

import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.AlbumPreferences;
import com.bemonovoid.playqd.core.model.MoveResult;
import com.bemonovoid.playqd.core.model.pageable.PageableRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;

public interface AlbumDao {

    Optional<Album> findOne(String id);

    Album getOne(String id);

    PageableResult<Album> getAlbums(PageableRequest pageableRequest);

    PageableResult<Album> getGenreAlbums(String genre, PageableRequest pageableRequest);

    PageableResult<Album> getArtistAlbums(String artistId, PageableRequest pageableRequest);

    PageableResult<Album> getArtistAlbumsWithNameContaining(
            String artistId, String albumName, PageableRequest pageableRequest);

    PageableResult<String> getGenres(PageableRequest pageableRequest);

    PageableResult<String> getGenresWithNameContaining(String genre, PageableRequest pageableRequest);

    Album save(Album albumEntity);

    void updateAlbum(Album album);

    void updateAlbumPreferences(String albumId, AlbumPreferences preferences);

    void saveAlbumImage(String albumId, byte[] binaryData);

    MoveResult move(String albumIdFrom, String albumIdTo);
}
