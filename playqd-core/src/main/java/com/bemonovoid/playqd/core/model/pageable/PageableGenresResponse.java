package com.bemonovoid.playqd.core.model.pageable;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PageableGenresResponse extends PageableResponse<String> {

    public PageableGenresResponse(PageableResult<String> result) {
        super(result);
    }

    @JsonProperty("genres")
    public List<String> getGenres() {
        return getContent();
    }

}
