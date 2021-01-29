package com.bemonovoid.playqd.datasource.jdbc.repository;

import com.bemonovoid.playqd.datasource.jdbc.entity.PlaybackHistoryEntity;
import org.springframework.data.repository.CrudRepository;

public interface PlaybackHistoryRepository extends CrudRepository<PlaybackHistoryEntity, Long> {
}
