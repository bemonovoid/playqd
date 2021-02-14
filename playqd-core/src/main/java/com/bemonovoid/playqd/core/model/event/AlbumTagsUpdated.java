package com.bemonovoid.playqd.core.model.event;

import com.bemonovoid.playqd.core.model.Album;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AlbumTagsUpdated extends ApplicationEvent {

    private final Album album;

    public AlbumTagsUpdated(Object source, Album album) {
        super(source);
        this.album = album;
    }
}
