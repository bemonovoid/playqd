package com.bemonovoid.playqd.data.dao;

import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.data.entity.AlbumEntity;

public interface AlbumDao {

    Optional<AlbumEntity> getOne(long id);

    List<AlbumEntity> getAll();

    List<AlbumEntity> getArtistAlbums(long artistId);

    AlbumEntity save(AlbumEntity albumEntity);

}
