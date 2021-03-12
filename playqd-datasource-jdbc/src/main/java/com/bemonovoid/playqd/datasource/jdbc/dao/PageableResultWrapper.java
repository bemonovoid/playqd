package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.util.List;

import com.bemonovoid.playqd.core.model.pageable.PageableResult;
import org.springframework.data.domain.Page;

class PageableResultWrapper<T> implements PageableResult<T> {

    private final Page<T> page;

    PageableResultWrapper(Page<T> page) {
        this.page = page;
    }

    @Override
    public int getTotalPages() {
        return page.getTotalPages();
    }

    @Override
    public long getTotalElements() {
        return page.getTotalElements();
    }

    @Override
    public int getPage() {
        return page.getNumber();
    }

    @Override
    public int getPageSIze() {
        return page.getSize();
    }

    @Override
    public List<T> getContent() {
        return page.getContent();
    }
}
