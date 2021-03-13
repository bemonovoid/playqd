package com.bemonovoid.playqd.core.dao;

import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.model.pageable.FindSongsRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;

public interface SongDao {

    Song getOne(long songId);

    String getSongFileLocation(long songId);

    Optional<String> getAnyAlbumSongFileLocation(long albumId);

    PageableResult<Song> getSongs(FindSongsRequest request);

    List<String> getAlbumSongsFileLocations(long albumId);

    List<String> getArtistSongsFileLocations(long artistId);

    void updatePlayCount(long songId);

    void updateFavoriteFlag(long songId, boolean isFavorite);

}
