package com.bemonovoid.playqd.core.service;

import java.util.List;

import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.model.pageable.FindSongsRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;

public interface SongService {

    Song getSong(long songId);

    String getSongFileLocation(long songId);

    List<Song> getAlbumSongs(long albumId);

    PageableResult<Song> getSongs(FindSongsRequest request);

    void updatePlayCount(long songId);

    void updateFavoriteFlag(long songId, boolean isFavorite);
}
