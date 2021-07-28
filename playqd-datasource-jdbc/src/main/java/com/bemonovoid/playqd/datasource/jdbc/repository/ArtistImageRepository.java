package com.bemonovoid.playqd.datasource.jdbc.repository;

import java.util.UUID;

import com.bemonovoid.playqd.datasource.jdbc.entity.ArtistImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistImageRepository extends JpaRepository<ArtistImageEntity, UUID> {
}
