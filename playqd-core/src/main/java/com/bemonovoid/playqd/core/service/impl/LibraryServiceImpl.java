package com.bemonovoid.playqd.core.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.dao.LibraryDao;
import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.AlbumSongs;
import com.bemonovoid.playqd.core.model.Albums;
import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.Artists;
import com.bemonovoid.playqd.core.model.Genres;
import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.model.UpdateAlbum;
import com.bemonovoid.playqd.core.model.UpdateArtist;
import com.bemonovoid.playqd.core.model.event.AlbumTagsUpdated;
import com.bemonovoid.playqd.core.model.event.ArtistTagsUpdated;
import com.bemonovoid.playqd.core.model.query.AlbumSongsQuery;
import com.bemonovoid.playqd.core.model.query.AlbumsQuery;
import com.bemonovoid.playqd.core.model.query.SongFilter;
import com.bemonovoid.playqd.core.model.query.SongQuery;
import com.bemonovoid.playqd.core.service.ArtworkService;
import com.bemonovoid.playqd.core.service.LibraryService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
class LibraryServiceImpl implements LibraryService {

    private final LibraryDao libraryDao;
    private final ArtworkService artworkService;
    private final ApplicationEventPublisher eventPublisher;

    LibraryServiceImpl(LibraryDao libraryDao, ArtworkService artworkService, ApplicationEventPublisher eventPublisher) {
        this.libraryDao = libraryDao;
        this.artworkService = artworkService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Artists getArtists() {
        return new Artists(libraryDao.ofArtist().getAll());
    }

    @Override
    public Genres getGenres() {
        return new Genres(libraryDao.ofAlbum().getGenres());
    }

    @Override
    public Optional<Album> getAlbum(long albumId) {
        return libraryDao.ofAlbum().findOne(albumId);
    }

    @Override
    public Albums getAlbums(AlbumsQuery query) {
        List<Album> albums = Collections.emptyList();
        if (query.getArtistId() != null) {
            albums = libraryDao.ofAlbum().getArtistAlbums(query.getArtistId());
        } else if (query.getGenre() != null) {
            albums = libraryDao.ofAlbum().getAllByGenre(query.getGenre());
        }
        return new Albums(albums);
    }

    @Override
    public Optional<Song> getSong(long songId) {
        return libraryDao.ofSong().getOne(songId);
    }

    @Override
    public AlbumSongs getAlbumSongs(AlbumSongsQuery query) {
        List<Song> songs = libraryDao.ofSong().getAlbumSongs(query.getAlbumId());
        return new AlbumSongs(songs.get(0).getArtist(), songs.get(0).getAlbum(), songs);
    }

    @Override
    public List<Song> getSongs(SongQuery query) {
        if (SongFilter.PLAY_COUNT == query.getFilter()) {
            return libraryDao.ofSong().getTopPlayedSongs(query.getPageSize());
        } else if (SongFilter.LAST_PLAYED == query.getFilter()) {
            return libraryDao.ofSong().getTopRecentlyPlayedSongs(query.getPageSize());
        } else if (SongFilter.FAVORITES == query.getFilter()) {
            return libraryDao.ofSong().getFavoriteSongs(query.getPageSize());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void updateSongFavoriteStatus(long songId) {
        libraryDao.ofSong().updateFavoriteStatus(songId);
    }

    @Override
    public void updateArtist(long artistId, UpdateArtist updateArtist) {
        Artist artist;
        if (artistId == updateArtist.getId()) {
            artist = Artist.builder()
                    .id(artistId).name(updateArtist.getName()).country(updateArtist.getCountry()).build();
            libraryDao.ofArtist().updateArtist(artist);
        } else {
            libraryDao.ofArtist().move(artistId, updateArtist.getId());
            artist = libraryDao.ofArtist().getOne(updateArtist.getId());
        }
        eventPublisher.publishEvent(new ArtistTagsUpdated(this, artist));
    }

    @Override
    public void updateAlbum(long albumId, UpdateAlbum updateAlbum) {
        Album album = null;
        if (albumId == updateAlbum.getId()) {
            album = Album.builder()
                    .id(albumId)
                    .name(updateAlbum.getName())
                    .date(updateAlbum.getDate())
                    .genre(updateAlbum.getGenre())
                    .build();

            libraryDao.ofAlbum().updateAlbum(album);

            if (StringUtils.hasText(updateAlbum.getArtworkSrc())) {
                artworkService.updateAlbumArtwork(albumId, updateAlbum.getArtworkSrc());
            }

            if (updateAlbum.isOverrideSongNameWithFileName()) {
                libraryDao.ofSong().setShowAlbumSongNameAsFileName(albumId);
            }
        } else {
            libraryDao.ofAlbum().move(albumId, updateAlbum.getId());
            album = libraryDao.ofAlbum().getOne(updateAlbum.getId());
        }
        eventPublisher.publishEvent(new AlbumTagsUpdated(this, album));
    }
}
