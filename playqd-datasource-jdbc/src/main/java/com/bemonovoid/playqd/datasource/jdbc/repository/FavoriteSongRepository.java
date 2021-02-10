package com.bemonovoid.playqd.datasource.jdbc.repository;

import com.bemonovoid.playqd.datasource.jdbc.entity.FavoriteSongEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteSongRepository extends JpaRepository<FavoriteSongEntity, Long> {

    boolean existsBySongId(long songId);

    void deleteBySongId(long songId);
}
