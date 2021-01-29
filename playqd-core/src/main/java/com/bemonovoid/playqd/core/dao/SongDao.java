package com.bemonovoid.playqd.core.dao;

import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.model.Song;

public interface SongDao {

    Optional<Song> getOne(long id);

    List<Song> getArtistSongs(long artistId);

    List<Song> getAlbumSongs(long albumId);

    Optional<Song> getFirstSongInAlbum(long albumId);
}
