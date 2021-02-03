package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.bemonovoid.playqd.core.dao.ArtistDao;
import com.bemonovoid.playqd.core.dao.PlaybackHistoryDao;
import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.PlaybackHistoryArtist;
import com.bemonovoid.playqd.datasource.jdbc.entity.ArtistEntity;
import com.bemonovoid.playqd.datasource.jdbc.repository.ArtistRepository;
import org.springframework.stereotype.Component;

@Component
class ArtistDaoImpl implements ArtistDao {

    private final ArtistRepository repository;
    private final PlaybackHistoryDao playbackHistoryDao;

    ArtistDaoImpl(ArtistRepository repository, PlaybackHistoryDao playbackHistoryDao) {
        this.repository = repository;
        this.playbackHistoryDao = playbackHistoryDao;
    }

    @Override
    public Artist getOne(Long id) {
        ArtistEntity artistEntity = repository.findById(id).get();
        return ArtistHelper.fromEntity(artistEntity);
    }

    @Override
    public Optional<Artist> getByName(String name) {
        return repository.findByName(name).map(ArtistHelper::fromEntity);
    }

    @Override
    public List<Artist> getAll() {
        Map<Long, PlaybackHistoryArtist> artistPlaybackHistory = playbackHistoryDao.getArtistPlaybackHistory();
        return repository.findAll().stream()
                .map(e -> ArtistHelper.fromEntity(e, artistPlaybackHistory.get(e.getId())))
                .sorted(Comparator.comparing(Artist::getName))
                .collect(Collectors.toList());
    }

    @Override
    public void updateArtist(Long artistId, String mbArtistId, String country) {
        repository.findById(artistId).ifPresent(artistEntity -> {
            artistEntity.setMbArtistId(mbArtistId);
            artistEntity.setCountry(country);
            repository.save(artistEntity);
        });
    }
}
