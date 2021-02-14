package com.bemonovoid.playqd.core.model.event;

import com.bemonovoid.playqd.core.model.Artist;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ArtistTagsUpdated extends ApplicationEvent {

    private final Artist artist;

    public ArtistTagsUpdated(Object source, Artist artist) {
        super(source);
        this.artist = artist;
    }
}
