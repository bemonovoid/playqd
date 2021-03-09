package com.bemonovoid.playqd.core.service;

import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.model.query.SongQuery;

public interface SongService {

    Optional<Song> getSong(long songId);

    List<Song> getAlbumSongs(long albumId);

    List<Song> getSongs(SongQuery query);

    void updatePlayCount(long songId);

    void updateFavoriteFlag(long songId, boolean isFavorite);
}
