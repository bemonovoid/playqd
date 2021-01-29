package com.bemonovoid.playqd.data.repository;

import com.bemonovoid.playqd.data.entity.PlaybackHistoryEntity;
import org.springframework.data.repository.CrudRepository;

public interface JpaPlaybackHistoryRepository extends CrudRepository<PlaybackHistoryEntity, Long> {
}
