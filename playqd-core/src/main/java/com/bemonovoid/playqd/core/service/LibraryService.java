package com.bemonovoid.playqd.core.service;

import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.AlbumSongs;
import com.bemonovoid.playqd.core.model.Albums;
import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.Artists;
import com.bemonovoid.playqd.core.model.Genres;
import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.model.UpdateArtistRequest;
import com.bemonovoid.playqd.core.model.query.AlbumSongsQuery;
import com.bemonovoid.playqd.core.model.query.AlbumsQuery;
import com.bemonovoid.playqd.core.model.query.SongQuery;

public interface LibraryService {

    Artists getArtists();

    Genres getGenres();

    Optional<Album> getAlbum(long albumId);

    Albums getAlbums(AlbumsQuery query);

    Optional<Song> getSong(long songId);

    AlbumSongs getAlbumSongs(AlbumSongsQuery query);

    List<Song> getSongs(SongQuery query);

    void updateSongFavoriteStatus(long songId);

    void updateArtist(Artist artist);

    void updateAlbum(Album album);
}
