package com.bemonovoid.playqd.data.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.data.dao.ArtistDao;
import com.bemonovoid.playqd.data.entity.ArtistEntity;
import com.bemonovoid.playqd.data.repository.JpaArtistRepository;
import org.springframework.transaction.annotation.Transactional;

public class ArtistDaoImpl implements ArtistDao {

    private final JpaArtistRepository repository;

    public ArtistDaoImpl(JpaArtistRepository repository) {
        this.repository = repository;
    }

    @Override
    public ArtistEntity getOne(Long id) {
        return repository.findById(id).get();
    }

    @Override
    public Optional<ArtistEntity> getByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public List<ArtistEntity> getAll() {
        Iterable<ArtistEntity> artistsIter = repository.findAll();
        List<ArtistEntity> artists = new ArrayList<>();
        artistsIter.forEach(artists::add);
        return artists;
    }

    @Override
    public ArtistEntity save(ArtistEntity artistEntity) {
        return repository.save(artistEntity);
    }

    @Override
    @Transactional
    public void saveAll(Collection<ArtistEntity> artistEntity) {
        repository.saveAll(artistEntity);
    }
}
