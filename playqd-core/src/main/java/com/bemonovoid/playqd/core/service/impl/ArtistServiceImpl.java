package com.bemonovoid.playqd.core.service.impl;

import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.dao.ArtistDao;
import com.bemonovoid.playqd.core.dao.SongDao;
import com.bemonovoid.playqd.core.handler.AudioFileTagUpdater;
import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.BasicArtist;
import com.bemonovoid.playqd.core.model.pageable.FindArtistsRequest;
import com.bemonovoid.playqd.core.model.Image;
import com.bemonovoid.playqd.core.model.ImageSize;
import com.bemonovoid.playqd.core.model.MoveResult;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;
import com.bemonovoid.playqd.core.model.UpdateOptions;
import com.bemonovoid.playqd.core.service.ArtistService;
import com.bemonovoid.playqd.core.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
    public List<BasicArtist> getAllBasicArtists() {
        return artistDao.getAllBasicArtists();
    }

    @Override
    public PageableResult<Artist> getArtists(FindArtistsRequest request) {
        return artistDao.getAll(request);
    }

    @Override
    public Optional<Image> getImage(long artistId, ImageSize size, boolean findRemotely) {
        return imageService.getArtistImage(artistDao.getOne(artistId), size, findRemotely);
    }

    @Override
    public Artist move(long fromArtistId, long toArtistId, UpdateOptions updateOptions) {
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
