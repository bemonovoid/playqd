package com.bemonovoid.playqd.datasource.jdbc.repository;

import java.util.UUID;

import com.bemonovoid.playqd.core.exception.PlayqdEntityNotFoundException;
import com.bemonovoid.playqd.datasource.jdbc.entity.AlbumEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.MusicDatabaseUpdateLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicDatabaseUpdateLogRepository extends JpaRepository<MusicDatabaseUpdateLogEntity, UUID> {

    default MusicDatabaseUpdateLogEntity findOne(UUID id) {
        return findById(id).orElseThrow(() -> new PlayqdEntityNotFoundException(id.toString(), "scannerLog"));
    }

}
