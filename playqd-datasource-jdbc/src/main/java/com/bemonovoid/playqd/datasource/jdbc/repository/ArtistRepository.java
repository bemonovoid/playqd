package com.bemonovoid.playqd.datasource.jdbc.repository;

import java.util.Optional;

import com.bemonovoid.playqd.datasource.jdbc.entity.ArtistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<ArtistEntity, Long> {

    Optional<ArtistEntity> findByName(String name);
}
