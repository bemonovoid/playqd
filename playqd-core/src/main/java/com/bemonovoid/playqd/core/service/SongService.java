package com.bemonovoid.playqd.core.service;

import java.util.List;

import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.model.UpdateOptions;
import com.bemonovoid.playqd.core.model.pageable.FindSongsRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;

public interface SongService {

    Song getSong(String songId);

    String getSongFileLocation(String songId);

    List<String> getAlbumSongsFormats(String albumId);

    PageableResult<Song> getSongs(FindSongsRequest request);

    PageableResult<Song> getAlbumSongs(String albumId, FindSongsRequest request);

    PageableResult<Song> getArtistSongs(String artistId, FindSongsRequest request);

    void updateSong(Song song, UpdateOptions options);

    void updatePlayCount(String songId);

    void updateFavoriteFlag(String songId, boolean isFavorite);

    void moveSong(String songId, String albumIdTo, UpdateOptions updateOptions);
}
