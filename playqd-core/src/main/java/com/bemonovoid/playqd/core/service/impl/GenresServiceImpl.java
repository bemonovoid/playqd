package com.bemonovoid.playqd.core.service.impl;

import com.bemonovoid.playqd.core.dao.AlbumDao;
import com.bemonovoid.playqd.core.model.pageable.FindGenresRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;
import com.bemonovoid.playqd.core.model.pageable.SortRequest;
import com.bemonovoid.playqd.core.service.GenresService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
class GenresServiceImpl implements GenresService {

    private final AlbumDao albumDao;

    GenresServiceImpl(AlbumDao albumDao) {
        this.albumDao = albumDao;
    }

    @Override
    public PageableResult<String> getGenres(FindGenresRequest request) {
        SortRequest sort = SortRequest.builder().direction(request.getDirection()).build();
        PageableRequest pageableRequest = new PageableRequest(request.getPage(), request.getSize(), sort);
        if (StringUtils.hasText(request.getName())) {
            return albumDao.getGenresWithNameContaining(request.getName(), pageableRequest);
        }
        return albumDao.getGenres(pageableRequest);
    }

}
