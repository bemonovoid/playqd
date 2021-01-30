package com.bemonovoid.playqd.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SongPlaybackEndedEvent extends ApplicationEvent {

    private final long songId;

    public SongPlaybackEndedEvent(Object source, long songId) {
        super(source);
        this.songId = songId;
    }
}
