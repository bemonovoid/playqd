package com.bemonovoid.playqd.datasource.jdbc.repository;

import java.util.List;
import java.util.UUID;

import com.bemonovoid.playqd.core.exception.PlayqdEntityNotFoundException;
import com.bemonovoid.playqd.core.helpers.EntityNameHelper;
import com.bemonovoid.playqd.datasource.jdbc.entity.ArtistEntity;
import com.bemonovoid.playqd.datasource.jdbc.projection.ArtistIdAndNameProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ArtistRepository extends JpaRepository<ArtistEntity, UUID> {

    default ArtistEntity findOne(UUID id) {
        return findById(id).orElseThrow(() -> new PlayqdEntityNotFoundException(id.toString(), "artist"));
    }

    @Query("SELECT a.id as id, a.name as name from ArtistEntity a")
    Page<ArtistIdAndNameProjection> findAllBasicArtists(Pageable pageable);

    Page<ArtistEntity> findByNameIgnoreCaseContaining(String name, Pageable pageable);

    @Query("SELECT s.artist.id FROM SongEntity s " +
            "WHERE s.playCount > 0 GROUP BY s.artist.id ORDER BY MAX(s.lastModifiedDate) DESC")
    Page<UUID> findRecentlyPlayedArtists(Pageable pageable);

    @Query("SELECT s.artist.id FROM SongEntity s GROUP BY s.artist.id ORDER BY MAX(s.playCount) DESC")
    Page<UUID> findMostPlayedArtists(Pageable pageable);
}
