package com.bemonovoid.playqd.core.service;

import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.model.pageable.FindSongsRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;

public interface SongService {

    Song getSong(long songId);

    String getSongFileLocation(long songId);

    PageableResult<Song> getSongs(FindSongsRequest request);

    void updatePlayCount(long songId);

    void updateFavoriteFlag(long songId, boolean isFavorite);
}
