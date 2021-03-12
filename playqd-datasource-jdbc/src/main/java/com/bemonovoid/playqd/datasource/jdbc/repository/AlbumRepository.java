package com.bemonovoid.playqd.datasource.jdbc.repository;

import com.bemonovoid.playqd.core.exception.PlayqdEntityNotFoundException;
import com.bemonovoid.playqd.core.helpers.EntityNameHelper;
import com.bemonovoid.playqd.datasource.jdbc.entity.AlbumEntity;
import com.bemonovoid.playqd.datasource.jdbc.projection.GenreProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlbumRepository extends JpaRepository<AlbumEntity, Long> {

    default AlbumEntity findOne(long id) {
        return findById(id).orElseThrow(() -> new PlayqdEntityNotFoundException(id, "album"));
    }

    default Page<AlbumEntity> findByArtistIdAndNameContaining(long artistId, String name, Pageable pageable) {
        return findByArtistIdAndSimpleNameContaining(artistId, EntityNameHelper.toLookUpName(name), pageable);
    }

    Page<AlbumEntity> findByArtistId(long artistId, Pageable pageable);

    Page<AlbumEntity> findByArtistIdAndSimpleNameContaining(long artistId, String name, Pageable pageable);

    Page<AlbumEntity> findByGenreEquals(String genre, Pageable pageable);

    Page<GenreProjection> findDistinctByGenreIgnoreCaseContaining(String name, Pageable pageable);

    @Query("SELECT DISTINCT a.genre FROM AlbumEntity a WHERE a.genre <> ''")
    Page<String> findDistinctGenre(Pageable pageable);
}
