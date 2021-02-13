package com.bemonovoid.playqd.core.service.impl;

import java.util.Map;

import com.bemonovoid.playqd.core.dao.PlaybackHistoryDao;
import com.bemonovoid.playqd.core.model.event.SongPlayed;
import com.bemonovoid.playqd.core.model.PlaybackHistoryArtist;
import com.bemonovoid.playqd.core.service.PlaybackHistoryService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
class PlaybackHistoryServiceImpl implements PlaybackHistoryService {

    private final ApplicationEventPublisher publisher;
    private final PlaybackHistoryDao playbackHistoryDao;

    PlaybackHistoryServiceImpl(ApplicationEventPublisher publisher,
                               PlaybackHistoryDao playbackHistoryDao) {
        this.publisher = publisher;
        this.playbackHistoryDao = playbackHistoryDao;
    }

    @Override
    public void updatePlaybackHistoryWithSongEnded(long songId) {
        publisher.publishEvent(new SongPlayed(this, songId));
    }

    @Override
    public Map<Long, PlaybackHistoryArtist> getArtistPlaybackHistory() {
        return playbackHistoryDao.getArtistPlaybackHistory();
    }
}
