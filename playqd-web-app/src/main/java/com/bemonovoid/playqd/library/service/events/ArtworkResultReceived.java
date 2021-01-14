package com.bemonovoid.playqd.library.service.events;

import com.bemonovoid.playqd.online.search.ArtworkSearchResult;
import org.springframework.context.ApplicationEvent;

public class ArtworkResultReceived extends ApplicationEvent {

    private final Long albumId;
    private final ArtworkSearchResult artworkSearchResult;

    public ArtworkResultReceived(Object source, Long albumId, ArtworkSearchResult artworkSearchResult) {
        super(source);
        this.albumId = albumId;
        this.artworkSearchResult = artworkSearchResult;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public ArtworkSearchResult getArtworkSearchResult() {
        return artworkSearchResult;
    }
}
