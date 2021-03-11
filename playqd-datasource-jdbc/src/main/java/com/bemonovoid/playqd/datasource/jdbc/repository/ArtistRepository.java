package com.bemonovoid.playqd.datasource.jdbc.repository;

import java.util.Optional;

import com.bemonovoid.playqd.core.exception.PlayqdEntityNotFoundException;
import com.bemonovoid.playqd.core.helpers.EntityNameHelper;
import com.bemonovoid.playqd.datasource.jdbc.entity.ArtistEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ArtistRepository extends JpaRepository<ArtistEntity, Long> {

    default ArtistEntity findOne(long id) {
        return findById(id).orElseThrow(() -> new PlayqdEntityNotFoundException(id, "artist"));
    }

    Page<ArtistEntity> findBySimpleNameContaining(String name, Pageable pageRequest);

    @Query("SELECT p.song.artist.id " +
        "FROM PlaybackInfoEntity p " +
        "WHERE p.createdBy = ?1 AND p.playCount > 0 " +
        "GROUP BY p.song.artist.id " +
        "ORDER BY MAX(p.createdDate) DESC")
    Page<Long> findRecentlyPlayedArtists(String createdBy, PageRequest pageRequest);

    @Query("SELECT p.song.artist.id " +
            "FROM PlaybackInfoEntity p " +
            "WHERE p.createdBy = ?1 " +
            "GROUP BY p.song.artist.id " +
            "ORDER BY MAX(p.playCount) DESC")
    Page<Long> findMostPlayedArtists(String createdBy, PageRequest pageRequest);
}
