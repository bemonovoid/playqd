package com.bemonovoid.playqd.core.service;

import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.model.AlbumSongs;
import com.bemonovoid.playqd.core.model.Genres;
import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.model.query.AlbumSongsQuery;
import com.bemonovoid.playqd.core.model.query.SongQuery;

public interface LibraryService {

    Genres getGenres();


    Optional<Song> getSong(long songId);

    AlbumSongs getAlbumSongs(AlbumSongsQuery query);

    List<Song> getSongs(SongQuery query);

    void updateSongFavoriteStatus(long songId);
}
