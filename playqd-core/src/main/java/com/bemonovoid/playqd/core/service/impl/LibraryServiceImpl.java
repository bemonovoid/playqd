package com.bemonovoid.playqd.core.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.dao.LibraryDao;
import com.bemonovoid.playqd.core.model.AlbumSongs;
import com.bemonovoid.playqd.core.model.Genres;
import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.model.query.AlbumSongsQuery;
import com.bemonovoid.playqd.core.model.query.SongFilter;
import com.bemonovoid.playqd.core.model.query.SongQuery;
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


    @Override
    public Optional<Song> getSong(long songId) {
        return libraryDao.ofSong().getOne(songId);
    }

    @Override
    public AlbumSongs getAlbumSongs(AlbumSongsQuery query) {
        List<Song> songs = libraryDao.ofSong().getAlbumSongs(query.getAlbumId());
        return new AlbumSongs(songs.get(0).getArtist(), songs.get(0).getAlbum(), songs);
    }

    @Override
    public List<Song> getSongs(SongQuery query) {
        if (SongFilter.PLAY_COUNT == query.getFilter()) {
            return libraryDao.ofSong().getTopPlayedSongs(query.getPageSize());
        } else if (SongFilter.RECENTLY_PLAYED == query.getFilter()) {
            return libraryDao.ofSong().getTopRecentlyPlayedSongs(query.getPageSize());
        } else if (SongFilter.RECENTLY_ADDED == query.getFilter()) {
            return libraryDao.ofSong().getRecentlyAdded(query.getPageSize());
        } else if (SongFilter.FAVORITES == query.getFilter()) {
            return libraryDao.ofSong().getFavoriteSongs(query.getPageSize());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void updateSongFavoriteStatus(long songId) {
        libraryDao.ofSong().updateFavoriteStatus(songId);
    }

}
