package com.bemonovoid.playqd.core.model.pageable;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.bemonovoid.playqd.core.model.Album;
import lombok.Getter;

@Getter
public class PageableAlbumResponse extends PageableResponse<Album> {

    public PageableAlbumResponse(PageableResult<Album> pageableResult) {
        super(pageableResult);
    }

    @JsonProperty("albums")
    public List<Album> getAlbums() {
        return getContent();
    }

}
