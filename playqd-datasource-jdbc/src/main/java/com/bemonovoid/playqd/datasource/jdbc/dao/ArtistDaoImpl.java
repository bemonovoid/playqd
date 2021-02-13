package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.bemonovoid.playqd.core.dao.ArtistDao;
import com.bemonovoid.playqd.core.dao.PlaybackHistoryDao;
import com.bemonovoid.playqd.core.exception.PlayqdEntityNotFoundException;
import com.bemonovoid.playqd.core.helpers.EntityNameHelper;
import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.PlaybackHistoryArtist;
import com.bemonovoid.playqd.datasource.jdbc.entity.ArtistEntity;
import com.bemonovoid.playqd.datasource.jdbc.projection.CountProjection;
import com.bemonovoid.playqd.datasource.jdbc.repository.ArtistRepository;
import com.bemonovoid.playqd.datasource.jdbc.repository.SongRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
class ArtistDaoImpl implements ArtistDao {

    private final ArtistRepository artistRepository;
    private final SongRepository songRepository;
    private final PlaybackHistoryDao playbackHistoryDao;

    ArtistDaoImpl(ArtistRepository artistRepository,
                  SongRepository songRepository,
                  PlaybackHistoryDao playbackHistoryDao) {
        this.artistRepository = artistRepository;
        this.songRepository = songRepository;
        this.playbackHistoryDao = playbackHistoryDao;
    }

    @Override
    public Artist getOne(long id) {
        ArtistEntity artistEntity = artistRepository.findById(id)
                .orElseThrow(() -> new PlayqdEntityNotFoundException(id, "artist"));
        return ArtistHelper.fromEntity(artistEntity);
    }

    @Override
    public List<Artist> getAll() {
        Map<Long, PlaybackHistoryArtist> artistPlaybackHistory = playbackHistoryDao.getArtistPlaybackHistory();
        Map<Long, CountProjection> counts = songRepository.getArtistAlbumSongCount();
        return artistRepository.findAll().stream()
                .map(e -> ArtistHelper.fromEntity(
                        e, new ArtistMetadata(counts.get(e.getId()), artistPlaybackHistory.get(e.getId()))))
                .sorted(Comparator.comparing(Artist::getName))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateArtist(Artist artist) {
        log.info("Updating artist with id='{}'.", artist.getId());
        ArtistEntity entity = artistRepository.findOne(artist.getId());
        if (shouldUpdate(entity.getName(), artist.getName())) {
            entity.setName(artist.getName());
            entity.setSimpleName(EntityNameHelper.toLookUpName(artist.getName()));
        }
        if (shouldUpdate(entity.getCountry(), artist.getCountry())) {
            entity.setCountry(artist.getCountry());
        }
        if (shouldUpdate(entity.getMbArtistId(), artist.getMbArtistId())) {
            entity.setMbArtistId(artist.getMbArtistId());
        }
        artistRepository.save(entity);
        log.info("Updating artist with id='{} completed.'", artist.getId());
    }

    private boolean shouldUpdate(String oldVal, String newVal) {
        return newVal != null && !newVal.isBlank() && !newVal.equalsIgnoreCase(oldVal);
    }
}
