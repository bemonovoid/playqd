package com.bemonovoid.playqd.core.dao;

import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.model.Song;

public interface SongDao {

    Optional<Song> getOne(long id);

    List<Song> getAlbumSongs(long albumId);

    Optional<Song> getFirstSongInAlbum(long albumId);

    List<Song> getTopPlayedSongs(int pageSize);

    List<Song> getTopRecentlyPlayedSongs(int pageSize);
}
