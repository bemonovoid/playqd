package com.bemonovoid.playqd.core.dao;

import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.AlbumPreferences;
import com.bemonovoid.playqd.core.model.MoveResult;
import com.bemonovoid.playqd.core.model.pageable.FindGenresRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;

public interface AlbumDao {

    Optional<Album> findOne(long id);

    Album getOne(long id);

    PageableResult<Album> getAlbums(PageableRequest pageableRequest);

    PageableResult<Album> getGenreAlbums(String genre, PageableRequest pageableRequest);

    PageableResult<Album> getArtistAlbums(long artistId, PageableRequest pageableRequest);

    PageableResult<Album> getArtistAlbumsWithNameContaining(
            long artistId, String albumName, PageableRequest pageableRequest);

    PageableResult<String> getGenres(PageableRequest pageableRequest);

    PageableResult<String> getGenresWithNameContaining(String genre, PageableRequest pageableRequest);

    Album save(Album albumEntity);

    void updateAlbum(Album album);

    void updateAlbumPreferences(long albumId, AlbumPreferences preferences);

    void saveAlbumImage(long albumId, byte[] binaryData);

    MoveResult move(long albumIdFrom, Long albumIdTo);
}
