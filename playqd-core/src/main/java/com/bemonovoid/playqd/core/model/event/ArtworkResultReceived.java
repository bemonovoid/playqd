package com.bemonovoid.playqd.core.model.event;

import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.ArtworkOnlineSearchResult;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ArtworkResultReceived extends ApplicationEvent {

    private final Album album;
    private final ArtworkOnlineSearchResult artworkOnlineSearchResult;

    public ArtworkResultReceived(Object source, Album album, ArtworkOnlineSearchResult artworkOnlineSearchResult) {
        super(source);
        this.album = album;
        this.artworkOnlineSearchResult = artworkOnlineSearchResult;
    }
}
