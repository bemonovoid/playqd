package com.bemonovoid.playqd.core.handler;

import com.bemonovoid.playqd.core.dao.PlaybackHistoryDao;
import com.bemonovoid.playqd.core.model.event.SongPlayed;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
class SongPlayedEventHandler implements ApplicationListener<SongPlayed> {

    private final PlaybackHistoryDao playbackHistoryDao;

    SongPlayedEventHandler(PlaybackHistoryDao playbackHistoryDao) {
        this.playbackHistoryDao = playbackHistoryDao;
    }

    @Override
    @Async
    public void onApplicationEvent(SongPlayed event) {
        playbackHistoryDao.save(event.getSongId());
    }
}
