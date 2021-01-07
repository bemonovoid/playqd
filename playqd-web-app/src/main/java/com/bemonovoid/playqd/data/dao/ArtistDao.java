package com.bemonovoid.playqd.data.dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.data.entity.ArtistEntity;

public interface ArtistDao {

    ArtistEntity getOne(Long id);

    Optional<ArtistEntity> getByName(String name);

    List<ArtistEntity> getAll();

    ArtistEntity save(ArtistEntity artistEntity);

    void saveAll(Collection<ArtistEntity> artistEntities);
}
