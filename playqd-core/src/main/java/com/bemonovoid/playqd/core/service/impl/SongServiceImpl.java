package com.bemonovoid.playqd.core.service.impl;

import java.util.Optional;

import com.bemonovoid.playqd.core.dao.SongDao;
import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.model.pageable.FindSongsRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;
import com.bemonovoid.playqd.core.service.SongService;
import org.springframework.stereotype.Component;

@Component
class SongServiceImpl implements SongService {

    private final SongDao songDao;

    SongServiceImpl(SongDao songDao) {
        this.songDao = songDao;
    }

    @Override
    public void updatePlayCount(long songId) {
        songDao.updatePlayCount(songId);
    }

    @Override
    public void updateFavoriteFlag(long songId, boolean isFavorite) {
        songDao.updateFavoriteFlag(songId, isFavorite);
    }

    @Override
    public Optional<Song> getSong(long songId) {
        return songDao.getOne(songId);
    }

    @Override
    public PageableResult<Song> getSongs(FindSongsRequest request) {
        return songDao.getSongs(request);
    }
}
