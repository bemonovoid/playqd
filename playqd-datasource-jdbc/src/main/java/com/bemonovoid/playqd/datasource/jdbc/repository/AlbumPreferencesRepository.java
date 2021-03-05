package com.bemonovoid.playqd.datasource.jdbc.repository;

import java.util.Optional;

import com.bemonovoid.playqd.datasource.jdbc.entity.AlbumPreferencesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumPreferencesRepository extends JpaRepository<AlbumPreferencesEntity, Long> {

    Optional<AlbumPreferencesEntity> findByAlbumId(long id);

    Optional<AlbumPreferencesEntity> findByAlbumIdAndCreatedBy(long albumId, String createdBy);
}
