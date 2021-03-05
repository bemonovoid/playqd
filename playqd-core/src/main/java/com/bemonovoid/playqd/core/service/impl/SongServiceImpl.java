package com.bemonovoid.playqd.core.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.dao.SongDao;
import com.bemonovoid.playqd.core.model.AlbumSongs;
import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.model.query.AlbumSongsQuery;
import com.bemonovoid.playqd.core.model.query.SongFilter;
import com.bemonovoid.playqd.core.model.query.SongQuery;
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
    public AlbumSongs getAlbumSongs(AlbumSongsQuery query) {
        List<Song> songs = songDao.getAlbumSongs(query.getAlbumId());
        return new AlbumSongs(songs.get(0).getArtist(), songs.get(0).getAlbum(), songs);
    }

    @Override
    public List<Song> getSongs(SongQuery query) {
        if (SongFilter.PLAY_COUNT == query.getFilter()) {
            return songDao.getTopPlayedSongs(query.getPageSize());
        } else if (SongFilter.RECENTLY_PLAYED == query.getFilter()) {
            return songDao.getRecentlyPlayedSongs(query.getPageSize());
        } else if (SongFilter.RECENTLY_ADDED == query.getFilter()) {
            return songDao.getRecentlyAdded(query.getPageSize());
        } else if (SongFilter.FAVORITES == query.getFilter()) {
            return songDao.getFavoriteSongs(query.getPageSize());
        } else {
            return Collections.emptyList();
        }
    }
}
