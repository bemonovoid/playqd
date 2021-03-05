package com.bemonovoid.playqd.core.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.dao.AlbumDao;
import com.bemonovoid.playqd.core.dao.SongDao;
import com.bemonovoid.playqd.core.handler.AudioFileTagUpdater;
import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.AlbumPreferences;
import com.bemonovoid.playqd.core.model.Albums;
import com.bemonovoid.playqd.core.model.Image;
import com.bemonovoid.playqd.core.model.ImageSize;
import com.bemonovoid.playqd.core.model.MoveResult;
import com.bemonovoid.playqd.core.model.UpdateOptions;
import com.bemonovoid.playqd.core.model.query.AlbumsQuery;
import com.bemonovoid.playqd.core.service.AlbumService;
import com.bemonovoid.playqd.core.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class AlbumServiceImpl implements AlbumService {

    private final AlbumDao albumDao;
    private final SongDao songDao;
    private final ImageService imageService;

    AlbumServiceImpl(AlbumDao albumDao,
                     SongDao songDao,
                     ImageService imageService) {
        this.albumDao = albumDao;
        this.songDao = songDao;
        this.imageService = imageService;
    }

    @Override
    public Optional<Album> getAlbum(long albumId) {
        return albumDao.findOne(albumId);
    }

    @Override
    public Albums getAlbums(AlbumsQuery query) {
        List<Album> albums = Collections.emptyList();
        if (query.getArtistId() != null) {
            albums = albumDao.getArtistAlbums(query.getArtistId());
        } else if (query.getGenre() != null) {
            albums = albumDao.getGenreAlbums(query.getGenre());
        }
        return new Albums(albums);
    }

    @Override
    public Optional<Image> getImage(long albumId, ImageSize size, boolean findRemotely) {
        Album album = albumDao.getOne(albumId);
        if (album.getImage() != null) {
            return Optional.of(album.getImage());
        }
        return imageService.getAlbumImage(album, size, findRemotely);
    }

    @Override
    public void updateAlbum(Album album, UpdateOptions updateOptions) {
        albumDao.updateAlbum(album);
        if (updateOptions.isUpdateAudioTags()) {
            AudioFileTagUpdater.updateAlbumTags(album, songDao.getAlbumSongsFileLocations(album.getId()));
        }
    }

    @Override
    public void moveAlbum(long albumIdFrom, long albumIdTo, UpdateOptions updateOptions) {
        MoveResult moveResult = albumDao.move(albumIdFrom, albumIdTo);
        if (updateOptions.isUpdateAudioTags()) {
            AudioFileTagUpdater.updateAlbumTags(moveResult.getNewAlbum(), moveResult.getMovedSongFiles());
        }
    }

    @Override
    public void updateAlbumPreferences(AlbumPreferences preferences) {
        albumDao.updateAlbumPreferences(preferences);
    }

}
