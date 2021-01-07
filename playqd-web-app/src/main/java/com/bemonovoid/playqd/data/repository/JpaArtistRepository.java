package com.bemonovoid.playqd.data.repository;

import java.util.Optional;

import com.bemonovoid.playqd.data.entity.ArtistEntity;
import org.springframework.data.repository.CrudRepository;

public interface JpaArtistRepository extends CrudRepository<ArtistEntity, Long> {

    Optional<ArtistEntity> findByName(String name);
}
