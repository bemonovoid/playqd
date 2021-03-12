package com.bemonovoid.playqd.core.dao;

import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.model.pageable.FindSongsRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;

public interface SongDao {

    Optional<Song> getOne(long id);

    Optional<String> getAnyAlbumSongFileLocation(long albumId);

    PageableResult<Song> getSongs(FindSongsRequest request);

    List<String> getArtistSongsFileLocations(long artistId);

    List<String> getAlbumSongsFileLocations(long albumId);

    void updatePlayCount(long songId);

    void updateFavoriteFlag(long songId, boolean isFavorite);

}
