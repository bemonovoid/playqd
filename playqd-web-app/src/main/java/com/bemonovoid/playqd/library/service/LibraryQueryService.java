package com.bemonovoid.playqd.library.service;

import com.bemonovoid.playqd.library.model.Album;
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

    Song getSong(SongQuery query);

    AlbumSongs getAlbumSongs(AlbumSongsQuery query);
}
