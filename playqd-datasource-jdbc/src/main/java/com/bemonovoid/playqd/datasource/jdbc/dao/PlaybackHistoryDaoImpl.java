package com.bemonovoid.playqd.datasource.jdbc.dao;

import com.bemonovoid.playqd.core.dao.PlaybackHistoryDao;
import com.bemonovoid.playqd.datasource.jdbc.entity.PlaybackHistoryEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.SongEntity;
import com.bemonovoid.playqd.datasource.jdbc.repository.PlaybackHistoryRepository;
import com.bemonovoid.playqd.datasource.jdbc.repository.SongRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class PlaybackHistoryDaoImpl implements PlaybackHistoryDao {

    private final SongRepository songRepository;
    private final PlaybackHistoryRepository playbackHistoryRepository;

    PlaybackHistoryDaoImpl(SongRepository songRepository, PlaybackHistoryRepository playbackHistoryRepository) {
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
