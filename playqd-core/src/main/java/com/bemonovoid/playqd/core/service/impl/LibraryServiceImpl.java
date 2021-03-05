package com.bemonovoid.playqd.core.service.impl;

import com.bemonovoid.playqd.core.dao.LibraryDao;
import com.bemonovoid.playqd.core.model.Genres;
import com.bemonovoid.playqd.core.service.LibraryService;
import org.springframework.stereotype.Component;

@Component
class LibraryServiceImpl implements LibraryService {

    private final LibraryDao libraryDao;

    LibraryServiceImpl(LibraryDao libraryDao) {
        this.libraryDao = libraryDao;
    }

    @Override
    public Genres getGenres() {
        return new Genres(libraryDao.ofAlbum().getGenres());
    }

}
