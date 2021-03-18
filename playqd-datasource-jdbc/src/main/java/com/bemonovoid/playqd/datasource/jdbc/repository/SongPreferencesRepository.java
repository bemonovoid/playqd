package com.bemonovoid.playqd.datasource.jdbc.repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.bemonovoid.playqd.datasource.jdbc.entity.SongPreferencesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongPreferencesRepository extends JpaRepository<SongPreferencesEntity, UUID> {

    default Map<String, SongPreferencesEntity> findAlbumSongsPreferences(UUID albumId, String createdBy) {
        return findByCreatedByAndSongAlbumId(createdBy, albumId).stream()
                .collect(Collectors.toMap(k -> k.getSong().getUUID(), v -> v));
    }

    List<SongPreferencesEntity> findByCreatedByAndSongAlbumId(String createdBy, UUID albumId);
}
