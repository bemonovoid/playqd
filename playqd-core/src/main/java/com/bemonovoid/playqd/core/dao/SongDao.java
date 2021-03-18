package com.bemonovoid.playqd.core.dao;

import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.model.pageable.PageableRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;

public interface SongDao {

    Song getOne(String songId);

    String getSongFileLocation(String songId);

    Optional<String> getAnyAlbumSongFileLocation(String albumId);

    PageableResult<Song> getSongs(PageableRequest pageableRequest);

    PageableResult<Song> getRecentlyAddedSongs(PageableRequest pageableRequest);

    PageableResult<Song> getRecentlyPlayedSongs(PageableRequest pageableRequest);

    PageableResult<Song> getMostPlayedSongs(PageableRequest pageableRequest);

    PageableResult<Song> getFavoriteSongs(PageableRequest pageableRequest);

    PageableResult<Song> getSongsWithNameContaining(String name, PageableRequest pageableRequest);

    List<Song> getAlbumSongs(String albumId);

    List<String> getAlbumSongsFileLocations(String albumId);

    List<String> getArtistSongsFileLocations(String artistId);

    Song moveSong(String songId, String albumId);

    void updateSong(Song song);

    void updatePlayCount(String songId);

    void updateFavoriteFlag(String songId, boolean isFavorite);
}
