package com.bemonovoid.playqd.datasource.jdbc.repository;

import java.util.List;

import com.bemonovoid.playqd.core.exception.PlayqdEntityNotFoundException;
import com.bemonovoid.playqd.datasource.jdbc.entity.AlbumEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlbumRepository extends JpaRepository<AlbumEntity, Long> {

    default AlbumEntity findOne(long id) {
        return findById(id).orElseThrow(() -> new PlayqdEntityNotFoundException(id, "album"));
    }

    List<AlbumEntity> findAllByArtistId(Long artistId);

    @Query("SELECT DISTINCT a.genre FROM AlbumEntity a WHERE a.genre <> ''")
    List<String> findDistinctGenre();

    List<AlbumEntity> findAllByGenreEquals(String genre);

}
