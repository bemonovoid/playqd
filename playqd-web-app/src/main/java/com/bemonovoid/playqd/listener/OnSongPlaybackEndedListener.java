package com.bemonovoid.playqd.listener;

import com.bemonovoid.playqd.core.dao.PlaybackHistoryDao;
import com.bemonovoid.playqd.event.SongPlaybackEndedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
class OnSongPlaybackEndedListener implements ApplicationListener<SongPlaybackEndedEvent> {

    private final PlaybackHistoryDao playbackHistoryDao;

    OnSongPlaybackEndedListener(PlaybackHistoryDao playbackHistoryDao) {
        this.playbackHistoryDao = playbackHistoryDao;
    }

    @Override
    @Async
    public void onApplicationEvent(SongPlaybackEndedEvent event) {
        playbackHistoryDao.save(event.getSongId());
    }
}
