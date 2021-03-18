package com.bemonovoid.playqd.datasource.jdbc.repository;

import java.util.Optional;
import java.util.UUID;

import com.bemonovoid.playqd.datasource.jdbc.entity.AlbumPreferencesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumPreferencesRepository extends JpaRepository<AlbumPreferencesEntity, UUID> {

    Optional<AlbumPreferencesEntity> findByAlbumId(UUID id);

    Optional<AlbumPreferencesEntity> findByAlbumIdAndCreatedBy(UUID albumId, String createdBy);
}
