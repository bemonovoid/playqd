package com.bemonovoid.playqd.library.service;

import java.util.Optional;

import com.bemonovoid.playqd.library.model.Album;
import com.bemonovoid.playqd.library.model.AlbumSongs;
import com.bemonovoid.playqd.library.model.Albums;
import com.bemonovoid.playqd.library.model.Artists;
import com.bemonovoid.playqd.library.model.Genres;
import com.bemonovoid.playqd.library.model.Song;
import com.bemonovoid.playqd.library.model.query.AlbumSongsQuery;
import com.bemonovoid.playqd.library.model.query.AlbumsQuery;
import com.bemonovoid.playqd.library.model.query.SongQuery;
import com.bemonovoid.playqd.online.search.ArtworkBinary;

public interface LibraryQueryService {

    Artists getArtists();

    Genres getGenres();

    Optional<Album> getAlbum(long albumId);

    Albums getAlbums(AlbumsQuery query);

    Optional<Song> getSong(SongQuery query);

    AlbumSongs getAlbumSongs(AlbumSongsQuery query);

    Optional<ArtworkBinary> getArtworkBySongId(long songId);

    Optional<ArtworkBinary> getArtworkByAlbumId(long albumId);
}
