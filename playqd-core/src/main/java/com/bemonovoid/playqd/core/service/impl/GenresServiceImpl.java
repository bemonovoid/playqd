package com.bemonovoid.playqd.core.service.impl;

import com.bemonovoid.playqd.core.dao.AlbumDao;
import com.bemonovoid.playqd.core.model.pageable.FindGenresRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;
import com.bemonovoid.playqd.core.service.GenresService;
import org.springframework.stereotype.Component;

@Component
class GenresServiceImpl implements GenresService {

    private final AlbumDao albumDao;

    GenresServiceImpl(AlbumDao albumDao) {
        this.albumDao = albumDao;
    }

    @Override
    public PageableResult<String> getGenres(FindGenresRequest request) {
        return albumDao.getGenres(request);
    }

}
