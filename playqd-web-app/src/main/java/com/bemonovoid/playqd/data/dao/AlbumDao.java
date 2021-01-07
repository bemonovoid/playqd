package com.bemonovoid.playqd.data.dao;

import java.util.List;

import com.bemonovoid.playqd.data.entity.AlbumEntity;

public interface AlbumDao {

    AlbumEntity getOne(long id);

    List<AlbumEntity> getAll();

    List<AlbumEntity> getArtistAlbums(long artistId);

    AlbumEntity save(AlbumEntity albumEntity);

}
