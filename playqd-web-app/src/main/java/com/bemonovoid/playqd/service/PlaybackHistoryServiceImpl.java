package com.bemonovoid.playqd.service;

import com.bemonovoid.playqd.core.dao.PlaybackHistoryDao;
import com.bemonovoid.playqd.core.service.PlaybackHistoryService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
class PlaybackHistoryServiceImpl implements PlaybackHistoryService {

    private final PlaybackHistoryDao playbackHistoryDao;

    PlaybackHistoryServiceImpl(PlaybackHistoryDao playbackHistoryDao) {
        this.playbackHistoryDao = playbackHistoryDao;
    }

    @Override
    @Async
    public void logListenerSong(long songId) {
        playbackHistoryDao.save(songId);
    }
}
