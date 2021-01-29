package com.bemonovoid.playqd.library.service.impl;

import com.bemonovoid.playqd.data.dao.PlaybackHistoryDao;
import com.bemonovoid.playqd.library.service.PlaybackHistoryService;
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
