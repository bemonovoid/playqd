package com.bemonovoid.playqd.core.model.pageable;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"totalElements", "totalPages", "page", "pageSize"})
public abstract class PageableResponse<T> {

    @JsonIgnore
    private final PageableResult<T> result;

    public PageableResponse(PageableResult<T> result) {
        this.result = result;
    }

    @JsonProperty(value = "totalPages")
    public int getTotalPages() {
        return result.getTotalPages();
    }

    @JsonProperty("totalElements")
    public long totalElements() {
        return result.getTotalElements();
    }

    @JsonProperty("page")
    public int getPage() {
        return result.getPage();
    }

    @JsonProperty("pageSize")
    public int getPageSize() {
        return result.getPageSIze();
    }

    @JsonIgnore
    public final List<T> getContent() {
        return result.getContent();
    }

}
