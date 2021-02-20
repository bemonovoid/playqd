package com.bemonovoid.playqd.core.service.impl;

import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.dao.ArtistDao;
import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.Image;
import com.bemonovoid.playqd.core.model.ImageSize;
import com.bemonovoid.playqd.core.model.UpdateArtist;
import com.bemonovoid.playqd.core.model.event.ArtistTagsUpdated;
import com.bemonovoid.playqd.core.service.ArtistService;
import com.bemonovoid.playqd.core.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class ArtistServiceImpl implements ArtistService {

    private final ArtistDao artistDao;
    private final ImageService imageService;
    private final ApplicationEventPublisher eventPublisher;

    ArtistServiceImpl(ImageService imageService, ArtistDao artistDao, ApplicationEventPublisher eventPublisher) {
        this.imageService = imageService;
        this.artistDao = artistDao;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<Artist> getArtists() {
        return artistDao.getAll();
    }

    @Override
    public Optional<Image> getImage(long artistId, ImageSize size, boolean findRemotely) {
        return imageService.getArtistImage(artistDao.getOne(artistId), size, findRemotely);
    }

    @Override
    public Artist move( long fromArtistId, long toArtistId) {
        artistDao.move(fromArtistId, toArtistId);
        Artist movedToArtist = artistDao.getOne(toArtistId);
        eventPublisher.publishEvent(new ArtistTagsUpdated(this, movedToArtist));
        return movedToArtist;
    }

    @Override
    public Artist updateArtist(UpdateArtist updateArtist) {
        Artist artist = Artist.builder()
                .id(updateArtist.getId())
                .name(updateArtist.getName())
                .country(updateArtist.getCountry())
                .build();
        if (artistDao.update(artist)) {
            eventPublisher.publishEvent(new ArtistTagsUpdated(this, artist));
        }
        return artistDao.getOne(updateArtist.getId());
    }

}
