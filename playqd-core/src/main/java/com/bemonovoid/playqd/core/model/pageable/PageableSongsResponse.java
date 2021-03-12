package com.bemonovoid.playqd.core.model.pageable;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.Song;
import lombok.Getter;

@Getter
public class PageableSongsResponse extends PageableResponse<Song> {

    public PageableSongsResponse(PageableResult<Song> pageableResult) {
        super(pageableResult);
    }

    @JsonProperty("songs")
    public List<Song> getArtists() {
        return getContent();
    }
}
