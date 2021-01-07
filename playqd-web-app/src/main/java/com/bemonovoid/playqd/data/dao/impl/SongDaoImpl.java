package com.bemonovoid.playqd.data.dao.impl;

import java.util.Collection;
import java.util.List;

import com.bemonovoid.playqd.data.dao.SongDao;
import com.bemonovoid.playqd.data.entity.SongEntity;
import com.bemonovoid.playqd.data.repository.JpaSongRepository;

public class SongDaoImpl implements SongDao {

    private final JpaSongRepository repository;

    public SongDaoImpl(JpaSongRepository repository) {
        this.repository = repository;
    }

    @Override
    public SongEntity getOne(long id) {
        return repository.findById(id).get();
    }

    @Override
    public List<SongEntity> getArtistSongs(long artistId) {
        return repository.findAllByArtistId(artistId);
    }

    @Override
    public SongEntity save(SongEntity songEntity) {
        return repository.save(songEntity);
    }

    @Override
    public void saveAll(Collection<SongEntity> songEntities) {
        repository.saveAll(songEntities);
    }
}
