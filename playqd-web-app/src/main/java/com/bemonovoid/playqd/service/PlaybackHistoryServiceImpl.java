package com.bemonovoid.playqd.service;

import java.util.Map;

import com.bemonovoid.playqd.core.dao.PlaybackHistoryDao;
import com.bemonovoid.playqd.core.model.PlaybackHistoryArtist;
import com.bemonovoid.playqd.core.service.PlaybackHistoryService;
import com.bemonovoid.playqd.event.SongPlaybackEndedEvent;
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
        publisher.publishEvent(new SongPlaybackEndedEvent(this, songId));
    }

    @Override
    public Map<Long, PlaybackHistoryArtist> getArtistPlaybackHistory() {
        return playbackHistoryDao.getArtistPlaybackHistory();
    }
}
