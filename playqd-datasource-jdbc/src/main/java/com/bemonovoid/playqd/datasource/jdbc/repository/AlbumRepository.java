package com.bemonovoid.playqd.datasource.jdbc.repository;

import java.util.UUID;

import com.bemonovoid.playqd.core.exception.PlayqdEntityNotFoundException;
import com.bemonovoid.playqd.core.helpers.EntityNameHelper;
import com.bemonovoid.playqd.datasource.jdbc.entity.AlbumEntity;
import com.bemonovoid.playqd.datasource.jdbc.projection.GenreProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlbumRepository extends JpaRepository<AlbumEntity, UUID> {

    default AlbumEntity findOne(UUID id) {
        return findById(id).orElseThrow(() -> new PlayqdEntityNotFoundException(id.toString(), "album"));
    }

    default Page<AlbumEntity> findByArtistIdAndNameContaining(UUID artistId, String name, Pageable pageable) {
        return findByArtistIdAndSimpleNameContaining(artistId, EntityNameHelper.toLookUpName(name), pageable);
    }

    default Page<AlbumEntity> findByNameContaining(String name, Pageable pageable) {
        return findBySimpleNameContaining(EntityNameHelper.toLookUpName(name), pageable);
    }

    Page<AlbumEntity> findByArtistId(UUID artistId, Pageable pageable);

    Page<AlbumEntity> findBySimpleNameContaining(String name, Pageable pageable);

    Page<AlbumEntity> findByArtistIdAndSimpleNameContaining(UUID artistId, String name, Pageable pageable);

    Page<AlbumEntity> findByGenreEquals(String genre, Pageable pageable);

    Page<GenreProjection> findDistinctByGenreIgnoreCaseContaining(String name, Pageable pageable);

    @Query("SELECT DISTINCT a.genre FROM AlbumEntity a WHERE a.genre <> ''")
    Page<String> findDistinctGenre(Pageable pageable);
}
