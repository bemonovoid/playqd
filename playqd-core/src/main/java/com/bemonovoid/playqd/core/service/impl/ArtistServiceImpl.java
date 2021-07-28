package com.bemonovoid.playqd.core.service.impl;

import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.dao.ArtistDao;
import com.bemonovoid.playqd.core.dao.SongDao;
import com.bemonovoid.playqd.core.handler.AudioFileTagUpdater;
import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.Image;
import com.bemonovoid.playqd.core.model.ImageInfo;
import com.bemonovoid.playqd.core.model.ImageSize;
import com.bemonovoid.playqd.core.model.MoveResult;
import com.bemonovoid.playqd.core.model.UpdateOptions;
import com.bemonovoid.playqd.core.model.pageable.FindArtistsRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;
import com.bemonovoid.playqd.core.model.pageable.SortBy;
import com.bemonovoid.playqd.core.model.pageable.SortRequest;
import com.bemonovoid.playqd.core.service.ArtistService;
import com.bemonovoid.playqd.core.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
class ArtistServiceImpl implements ArtistService {

    private final ArtistDao artistDao;
    private final SongDao songDao;
    private final ImageService imageService;

    ArtistServiceImpl(ImageService imageService, ArtistDao artistDao, SongDao songDao) {
        this.imageService = imageService;
        this.artistDao = artistDao;
        this.songDao = songDao;
    }

    @Override
    public void addImages(String artistId, List<ImageInfo> images) {
        artistDao.addImages(artistId, images);
    }

    @Override
    public Artist getArtist(String artistId) {
        return artistDao.getOne(artistId);
    }

    @Override
    public PageableResult<Artist> getAllBasicArtists() {
        PageableRequest pageableRequest = new PageableRequest(0, Integer.MAX_VALUE, SortRequest.builder().build());
        return artistDao.getBasicArtists(pageableRequest);
    }

    @Override
    public PageableResult<Artist> getArtists(FindArtistsRequest request) {
        SortBy sortBy = SortBy.NAME;
        SortRequest sortRequest = SortRequest.builder().direction(request.getDirection()).build();
        if (request.getSortBy() != null) {
            sortBy = SortBy.valueOf(request.getSortBy().name());
        }

        PageableRequest pageableRequest = new PageableRequest(request.getPage(), request.getSize(), sortRequest);

        if (StringUtils.hasText(request.getName())) {
            return artistDao.getArtistsWithNameContaining(request.getName(), pageableRequest);
        }
        if (SortBy.RECENTLY_PLAYED == sortBy) {
            return artistDao.getRecentlyPlayedArtists(pageableRequest);
        } else if (SortBy.MOST_PLAYED == sortBy) {
            return artistDao.getMostPlayedArtists(pageableRequest);
        } else {
            return artistDao.getArtists(pageableRequest);
        }
    }

    @Override
    public List<Image> findImages(String artistId) {
        return imageService.findArtistImages(artistDao.getOne(artistId));
    }

    @Override
    public Artist move(String fromArtistId, String toArtistId, UpdateOptions updateOptions) {
        MoveResult moveResult = artistDao.move(fromArtistId, toArtistId);
        if (updateOptions.isUpdateAudioTags()) {
            AudioFileTagUpdater.updateArtistTags(moveResult.getNewArtist(), moveResult.getMovedSongFiles());
        }
        return moveResult.getNewArtist();
    }

    @Override
    public Artist updateArtist(Artist artist, UpdateOptions updateOptions) {
        artistDao.update(artist);
        if (updateOptions.isUpdateAudioTags()) {
            AudioFileTagUpdater.updateArtistTags(artist, songDao.getArtistSongsFileLocations(artist.getId()));
        }
        return artistDao.getOne(artist.getId());
    }

}
