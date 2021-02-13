package com.bemonovoid.playqd.core.model.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SongPlayed extends ApplicationEvent {

    private final long songId;

    public SongPlayed(Object source, long songId) {
        super(source);
        this.songId = songId;
    }
}
