package com.bemonovoid.playqd.data.dao.impl;

import com.bemonovoid.playqd.data.dao.PlaybackHistoryDao;
import com.bemonovoid.playqd.data.entity.PlaybackHistoryEntity;
import com.bemonovoid.playqd.data.entity.SongEntity;
import com.bemonovoid.playqd.data.repository.JpaPlaybackHistoryRepository;
import com.bemonovoid.playqd.data.repository.JpaSongRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class PlaybackHistoryDaoImpl implements PlaybackHistoryDao {

    private final JpaSongRepository songRepository;
    private final JpaPlaybackHistoryRepository playbackHistoryRepository;

    PlaybackHistoryDaoImpl(JpaSongRepository songRepository, JpaPlaybackHistoryRepository playbackHistoryRepository) {
        this.songRepository = songRepository;
        this.playbackHistoryRepository = playbackHistoryRepository;
    }

    @Override
    @Transactional
    public Long save(long songId) {
        SongEntity songEntity = songRepository.findById(songId).get();
        PlaybackHistoryEntity playbackHistoryEntity = new PlaybackHistoryEntity();
        playbackHistoryEntity.setSong(songEntity);
        return playbackHistoryRepository.save(playbackHistoryEntity).getId();
    }
}
