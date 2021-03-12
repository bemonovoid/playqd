package com.bemonovoid.playqd.core.model.pageable;

import java.util.List;

public interface PageableResult<T> {

    int getTotalPages();

    long getTotalElements();

    int getPage();

    int getPageSIze();

    List<T> getContent();
}
