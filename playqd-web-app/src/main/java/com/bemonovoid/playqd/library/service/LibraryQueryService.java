package com.bemonovoid.playqd.library.service;

import java.util.Optional;

import com.bemonovoid.playqd.library.model.Album;
import com.bemonovoid.playqd.library.model.AlbumArtwork;
import com.bemonovoid.playqd.library.model.AlbumSongs;
import com.bemonovoid.playqd.library.model.ArtistAlbums;
import com.bemonovoid.playqd.library.model.Artists;
import com.bemonovoid.playqd.library.model.Song;
import com.bemonovoid.playqd.library.model.query.AlbumSongsQuery;
import com.bemonovoid.playqd.library.model.query.ArtistAlbumsQuery;
import com.bemonovoid.playqd.library.model.query.SongQuery;

public interface LibraryQueryService {

    Artists getArtists();

    Album getAlbum(long albumId);

    ArtistAlbums getArtistAlbums(ArtistAlbumsQuery query);

    Optional<Song> getSong(SongQuery query);

    Optional<AlbumArtwork> getArtworkByAlbumId(long albumId);

    Optional<AlbumArtwork> getArtworkBySongId(long songId);

    AlbumSongs getAlbumSongs(AlbumSongsQuery query);
}
