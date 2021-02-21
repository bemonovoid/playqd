package com.bemonovoid.playqd.core.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.dao.AlbumDao;
import com.bemonovoid.playqd.core.dao.SongDao;
import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.Albums;
import com.bemonovoid.playqd.core.model.Image;
import com.bemonovoid.playqd.core.model.ImageSize;
import com.bemonovoid.playqd.core.model.UpdateAlbum;
import com.bemonovoid.playqd.core.model.event.AlbumTagsUpdated;
import com.bemonovoid.playqd.core.model.query.AlbumsQuery;
import com.bemonovoid.playqd.core.service.AlbumService;
import com.bemonovoid.playqd.core.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class AlbumServiceImpl implements AlbumService {

    private final AlbumDao albumDao;
    private final SongDao songDao;
    private final ImageService imageService;
    private final ApplicationEventPublisher eventPublisher;

    AlbumServiceImpl(AlbumDao albumDao,
                     SongDao songDao,
                     ImageService imageService,
                     ApplicationEventPublisher eventPublisher) {
        this.albumDao = albumDao;
        this.songDao = songDao;
        this.imageService = imageService;
        this.eventPublisher = eventPublisher;
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
    public void updateAlbum(UpdateAlbum updateAlbum) {
        Album album;
        if (updateAlbum.getMoveToAlbumId() == null) {
            album = Album.builder()
                    .id(updateAlbum.getId())
                    .name(updateAlbum.getName())
                    .date(updateAlbum.getDate())
                    .genre(updateAlbum.getGenre())
                    .build();

            albumDao.updateAlbum(album);

//            if (StringUtils.hasText(updateAlbum.getArtworkSrc())) {
//                imageService.updateAlbumArtwork(albumId, updateAlbum.getArtworkSrc());
//            }

            if (updateAlbum.isOverrideSongNameWithFileName()) {
                songDao.setShowAlbumSongNameAsFileName(updateAlbum.getId());
            }
        } else {
            albumDao.move(updateAlbum.getId(), updateAlbum.getMoveToAlbumId());
            album = albumDao.getOne(updateAlbum.getId());
        }
        eventPublisher.publishEvent(new AlbumTagsUpdated(this, album));
    }


}
