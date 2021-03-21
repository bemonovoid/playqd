package com.bemonovoid.playqd.core.service.impl;

import java.util.Optional;

import com.bemonovoid.playqd.core.dao.AlbumDao;
import com.bemonovoid.playqd.core.dao.SongDao;
import com.bemonovoid.playqd.core.handler.AudioFileTagUpdater;
import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.AlbumPreferences;
import com.bemonovoid.playqd.core.model.Image;
import com.bemonovoid.playqd.core.model.ImageSize;
import com.bemonovoid.playqd.core.model.MoveResult;
import com.bemonovoid.playqd.core.model.UpdateOptions;
import com.bemonovoid.playqd.core.model.pageable.FindAlbumRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;
import com.bemonovoid.playqd.core.model.pageable.SortBy;
import com.bemonovoid.playqd.core.model.pageable.SortRequest;
import com.bemonovoid.playqd.core.service.AlbumService;
import com.bemonovoid.playqd.core.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
    public Optional<Album> getAlbum(String albumId) {
        return albumDao.findOne(albumId);
    }

    @Override
    public PageableResult<Album> getAlbums(FindAlbumRequest request) {

        SortRequest sort = SortRequest.builder()
                .sortBy(SortBy.valueOf(request.getSortBy().name())).direction(request.getDirection()).build();

        PageableRequest pageable = new PageableRequest(request.getPage(), request.getSize(), sort);

        if (request.getArtistId() != null) {
            if (StringUtils.hasText(request.getName())) {
                return albumDao.getArtistAlbumsWithNameContaining(request.getArtistId(), request.getName(), pageable);
            } else {
                return albumDao.getArtistAlbums(request.getArtistId(), pageable);
            }
        } else if (StringUtils.hasText(request.getName())) {
            return albumDao.getAlbumsWithNameContaining(request.getName(), pageable);
        } else if (StringUtils.hasText(request.getGenre())) {
            return albumDao.getGenreAlbums(request.getGenre(), pageable);
        } else {
            return albumDao.getAlbums(pageable);
        }
    }

    @Override
    public Optional<Image> getImage(String albumId, ImageSize size, boolean findRemotely) {
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
    public void moveAlbum(String albumIdFrom, String albumIdTo, UpdateOptions updateOptions) {
        MoveResult moveResult = albumDao.move(albumIdFrom, albumIdTo);
        if (updateOptions.isUpdateAudioTags()) {
            AudioFileTagUpdater.updateAlbumTags(moveResult.getNewAlbum(), moveResult.getMovedSongFiles());
        }
    }

    @Override
    public void updateAlbumPreferences(String albumId, AlbumPreferences preferences) {
        albumDao.updateAlbumPreferences(albumId, preferences);
    }

}
