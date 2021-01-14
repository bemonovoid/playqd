package com.bemonovoid.playqd.library.service;

import java.util.Optional;

import com.bemonovoid.playqd.library.model.Album;
import com.bemonovoid.playqd.library.model.AlbumSongs;
import com.bemonovoid.playqd.library.model.ArtistAlbums;
import com.bemonovoid.playqd.library.model.Artists;
import com.bemonovoid.playqd.library.model.Song;
import com.bemonovoid.playqd.library.model.query.AlbumSongsQuery;
import com.bemonovoid.playqd.library.model.query.ArtistAlbumsQuery;
import com.bemonovoid.playqd.library.model.query.SongQuery;
import com.bemonovoid.playqd.online.search.ArtworkBinary;

public interface LibraryQueryService {

    Artists getArtists();

    Album getAlbum(long albumId);

    ArtistAlbums getArtistAlbums(ArtistAlbumsQuery query);

    Optional<Song> getSong(SongQuery query);

    AlbumSongs getAlbumSongs(AlbumSongsQuery query);

    Optional<ArtworkBinary> getArtworkBySongId(long songId);

    Optional<ArtworkBinary> getArtworkByAlbumId(long albumId);
}
