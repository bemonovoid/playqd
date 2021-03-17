package com.bemonovoid.playqd.datasource.jdbc.repository;

import java.util.List;

import com.bemonovoid.playqd.core.exception.PlayqdEntityNotFoundException;
import com.bemonovoid.playqd.core.helpers.EntityNameHelper;
import com.bemonovoid.playqd.datasource.jdbc.entity.ArtistEntity;
import com.bemonovoid.playqd.datasource.jdbc.projection.ArtistIdAndNameProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ArtistRepository extends JpaRepository<ArtistEntity, Long> {

    default ArtistEntity findOne(long id) {
        return findById(id).orElseThrow(() -> new PlayqdEntityNotFoundException(id, "artist"));
    }

    default Page<ArtistEntity> findByArtistNameContaining(String name, Pageable pageable) {
        return findBySimpleNameContaining(EntityNameHelper.toLookUpName(name), pageable);
    }

    @Query("SELECT a.id as id, a.name as name from ArtistEntity a")
    List<ArtistIdAndNameProjection> findAllBasicArtists();

    Page<ArtistEntity> findBySimpleNameContaining(String name, Pageable pageable);

    @Query("SELECT s.artist.id FROM SongEntity s " +
            "WHERE s.playCount > 0 GROUP BY s.artist.id ORDER BY MAX(s.lastModifiedDate) DESC")
    Page<Long> findRecentlyPlayedArtists(Pageable pageable);

    @Query("SELECT s.artist.id FROM SongEntity s GROUP BY s.artist.id ORDER BY MAX(s.playCount) DESC")
    Page<Long> findMostPlayedArtists(Pageable pageable);
}
