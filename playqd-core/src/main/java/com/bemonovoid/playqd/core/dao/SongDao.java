package com.bemonovoid.playqd.core.dao;

import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.model.pageable.PageableRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;

public interface SongDao {

    Song getOne(long songId);

    String getSongFileLocation(long songId);

    Optional<String> getAnyAlbumSongFileLocation(long albumId);

    PageableResult<Song> getSongs(PageableRequest pageableRequest);

    PageableResult<Song> getRecentlyAddedSongs(PageableRequest pageableRequest);

    PageableResult<Song> getRecentlyPlayedSongs(PageableRequest pageableRequest);

    PageableResult<Song> getMostPlayedSongs(PageableRequest pageableRequest);

    PageableResult<Song> getFavoriteSongs(PageableRequest pageableRequest);

    PageableResult<Song> getSongsWithNameContaining(String name, PageableRequest pageableRequest);

    List<Song> getAlbumSongs(long albumId);

    List<String> getAlbumSongsFileLocations(long albumId);

    List<String> getArtistSongsFileLocations(long artistId);

    void updatePlayCount(long songId);

    void updateFavoriteFlag(long songId, boolean isFavorite);

}
