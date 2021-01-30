package com.bemonovoid.playqd.service;

import com.bemonovoid.playqd.core.service.PlaybackHistoryService;
import com.bemonovoid.playqd.event.SongPlaybackEndedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
class PlaybackHistoryServiceImpl implements PlaybackHistoryService {

    private final ApplicationEventPublisher publisher;

    PlaybackHistoryServiceImpl(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void updatePlaybackHistoryWithSongEnded(long songId) {
        publisher.publishEvent(new SongPlaybackEndedEvent(this, songId));
    }
}
