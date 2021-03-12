package com.bemonovoid.playqd.core.model.pageable;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.bemonovoid.playqd.core.model.Artist;
import lombok.Getter;

@Getter
public class PageableArtistsResponse extends PageableResponse<Artist> {

    public PageableArtistsResponse(PageableResult<Artist> pageableResult) {
        super(pageableResult);
    }

    @JsonProperty("artists")
    public List<Artist> getArtists() {
        return getContent();
    }
}
