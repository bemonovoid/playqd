package com.bemonovoid.playqd.datasource.jdbc.repository;

import com.bemonovoid.playqd.core.exception.PlayqdEntityNotFoundException;
import com.bemonovoid.playqd.datasource.jdbc.entity.ArtistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<ArtistEntity, Long> {

    default ArtistEntity findOne(long id) {
        return findById(id).orElseThrow(() -> new PlayqdEntityNotFoundException(id, "artist"));
    }
}
