package com.bemonovoid.playqd.core.service;

import com.bemonovoid.playqd.core.model.pageable.FindGenresRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;

public interface GenresService {

    PageableResult<String> getGenres(FindGenresRequest request);

}
