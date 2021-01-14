package com.bemonovoid.playqd.data.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.data.dao.AlbumDao;
import com.bemonovoid.playqd.data.entity.AlbumEntity;
import com.bemonovoid.playqd.data.repository.JpaAlbumRepository;

public class AlbumDaoImpl implements AlbumDao {

    private final JpaAlbumRepository repository;

    public AlbumDaoImpl(JpaAlbumRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<AlbumEntity> getOne(long id) {
        return repository.findById(id);
    }

    @Override
    public List<AlbumEntity> getAll() {
        Iterable<AlbumEntity> albumsIter = repository.findAll();
        List<AlbumEntity> albums = new ArrayList<>();
        albumsIter.forEach(albums::add);
        return albums;
    }

    @Override
    public List<AlbumEntity> getArtistAlbums(long artistId) {
        return repository.findAllByArtistId(artistId);
    }

    @Override
    public AlbumEntity save(AlbumEntity albumEntity) {
        return repository.save(albumEntity);
    }
}
