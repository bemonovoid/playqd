package com.bemonovoid.playqd.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.dao.AlbumDao;
import com.bemonovoid.playqd.core.dao.ArtistDao;
import com.bemonovoid.playqd.core.dao.SongDao;
import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.AlbumSongs;
import com.bemonovoid.playqd.core.model.Albums;
import com.bemonovoid.playqd.core.model.Artists;
import com.bemonovoid.playqd.core.model.Genres;
import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.model.query.AlbumSongsQuery;
import com.bemonovoid.playqd.core.model.query.AlbumsQuery;
import com.bemonovoid.playqd.core.model.query.SongFilter;
import com.bemonovoid.playqd.core.model.query.SongQuery;
import com.bemonovoid.playqd.core.service.LibraryQueryService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
public class LibraryQueryServiceImpl implements LibraryQueryService {

    private final ArtistDao artistDao;
    private final AlbumDao albumDao;
    private final SongDao songDao;

    LibraryQueryServiceImpl(ArtistDao artistDao, AlbumDao albumDao, SongDao songDao) {
        this.artistDao = artistDao;
        this.albumDao = albumDao;
        this.songDao = songDao;
    }

    @Override
    public Artists getArtists() {
        return new Artists(artistDao.getAll());
    }

    @Override
    public Genres getGenres() {
        return new Genres(albumDao.getGenres());
    }

    @Override
    public Optional<Album> getAlbum(long albumId) {
        return albumDao.getOne(albumId);
    }

    @Override
    public Albums getAlbums(AlbumsQuery query) {
        List<Album> albums = Collections.emptyList();
        if (query.getArtistId() != null) {
            albums = albumDao.getArtistAlbums(query.getArtistId());
        } else if (query.getGenre() != null) {
            albums = albumDao.getAllByGenre(query.getGenre());
        }
        return new Albums(albums);
    }

    @Override
    public Optional<Song> getSong(long songId) {
        return songDao.getOne(songId);
    }

    @Override
    public AlbumSongs getAlbumSongs(AlbumSongsQuery query) {
        List<Song> songs = songDao.getAlbumSongs(query.getAlbumId());
        return new AlbumSongs(songs.get(0).getArtist(), songs.get(0).getAlbum(), songs);
    }

    @Override
    public List<Song> getSongs(SongQuery query) {
        if (SongFilter.PLAY_COUNT == query.getFilter()) {
            return songDao.getTopPlayedSongs(query.getPageSize());
        } else if (SongFilter.LAST_PLAYED == query.getFilter()) {
            return songDao.getTopRecentlyPlayedSongs(query.getPageSize());
        } else {
            return Collections.emptyList();
        }
    }
}
