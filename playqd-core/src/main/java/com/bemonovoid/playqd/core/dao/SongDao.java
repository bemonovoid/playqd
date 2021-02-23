package com.bemonovoid.playqd.core.dao;

import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.model.Song;

public interface SongDao {

    Optional<Song> getOne(long id);

    Optional<Song> getFirstSongInAlbum(long albumId);

    List<Song> getAlbumSongs(long albumId);

    List<Song> getTopPlayedSongs(int pageSize);

    List<Song> getTopRecentlyPlayedSongs(int pageSize);

    List<Song> getRecentlyAdded(int pageSize);

    List<Song> getFavoriteSongs(int pageSize);

    List<String> getArtistSongsFileLocations(long artistId);

    List<String> getAlbumSongsFileLocations(long albumId);

    void updateFavoriteStatus(long songId);

    void setShowAlbumSongNameAsFileName(long albumId);
}
