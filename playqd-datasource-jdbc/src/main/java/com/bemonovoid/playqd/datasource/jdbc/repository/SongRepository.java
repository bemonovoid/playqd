package com.bemonovoid.playqd.datasource.jdbc.repository;

import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.datasource.jdbc.entity.SongEntity;
import org.springframework.data.repository.CrudRepository;

public interface SongRepository extends CrudRepository<SongEntity, Long> {

    List<SongEntity> findAllByArtistId(Long artistId);

    List<SongEntity> findAllByAlbumId(Long albumId);

    Optional<SongEntity> findFirstByAlbumId(long albumId);
}
