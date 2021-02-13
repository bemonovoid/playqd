package com.bemonovoid.playqd.core.model.event;

import com.bemonovoid.playqd.core.model.MusicBrainzTagValues;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MusicBrainzTagsUpdateAvailable extends ApplicationEvent {

    private final MusicBrainzTagValues musicBrainzTagValues;

    public MusicBrainzTagsUpdateAvailable(Object source, MusicBrainzTagValues musicBrainzTagValues) {
        super(source);
        this.musicBrainzTagValues = musicBrainzTagValues;
    }
}
