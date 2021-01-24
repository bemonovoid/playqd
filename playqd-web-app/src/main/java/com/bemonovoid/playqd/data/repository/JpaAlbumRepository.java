package com.bemonovoid.playqd.data.repository;

import java.util.List;

import com.bemonovoid.playqd.data.entity.AlbumEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface JpaAlbumRepository extends CrudRepository<AlbumEntity, Long> {

    List<AlbumEntity> findAllByArtistId(Long artistId);

    @Query("SELECT DISTINCT a.genre FROM AlbumEntity a WHERE a.genre <> ''")
    List<String> findDistinctGenre();

    List<AlbumEntity> findAllByGenreEquals(String genre);

}
