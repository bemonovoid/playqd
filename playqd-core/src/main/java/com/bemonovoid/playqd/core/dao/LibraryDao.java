package com.bemonovoid.playqd.core.dao;

public interface LibraryDao {

    ArtistDao ofArtist();

    AlbumDao ofAlbum();

    SongDao ofSong();

    PlaybackHistoryDao ofPlaybackHistory();
}
