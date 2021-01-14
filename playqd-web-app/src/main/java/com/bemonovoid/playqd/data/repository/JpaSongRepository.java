package com.bemonovoid.playqd.data.repository;

import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.data.entity.SongEntity;
import org.springframework.data.repository.CrudRepository;

public interface JpaSongRepository extends CrudRepository<SongEntity, Long> {

    List<SongEntity> findAllByArtistId(Long artistId);

    List<SongEntity> findAllByAlbumId(Long albumId);

    Optional<SongEntity> findFirstByAlbumId(long albumId);
}
