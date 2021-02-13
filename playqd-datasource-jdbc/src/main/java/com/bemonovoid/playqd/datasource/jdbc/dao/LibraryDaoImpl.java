package com.bemonovoid.playqd.datasource.jdbc.dao;

import com.bemonovoid.playqd.core.dao.AlbumDao;
import com.bemonovoid.playqd.core.dao.ArtistDao;
import com.bemonovoid.playqd.core.dao.LibraryDao;
import com.bemonovoid.playqd.core.dao.PlaybackHistoryDao;
import com.bemonovoid.playqd.core.dao.SongDao;
import org.springframework.stereotype.Component;

@Component
class LibraryDaoImpl implements LibraryDao {

    private final ArtistDao artistDao;
    private final AlbumDao albumDao;
    private final SongDao songDao;
    private final PlaybackHistoryDao playbackHistoryDao;

    LibraryDaoImpl(ArtistDao artistDao, AlbumDao albumDao, SongDao songDao, PlaybackHistoryDao playbackHistoryDao) {
        this.artistDao = artistDao;
        this.albumDao = albumDao;
        this.songDao = songDao;
        this.playbackHistoryDao = playbackHistoryDao;
    }

    @Override
    public ArtistDao ofArtist() {
        return artistDao;
    }

    @Override
    public AlbumDao ofAlbum() {
        return albumDao;
    }

    @Override
    public SongDao ofSong() {
        return songDao;
    }

    @Override
    public PlaybackHistoryDao ofPlaybackHistory() {
        return playbackHistoryDao;
    }
}
