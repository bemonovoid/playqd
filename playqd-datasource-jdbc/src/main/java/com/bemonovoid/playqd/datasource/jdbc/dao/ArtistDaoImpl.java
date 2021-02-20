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
import com.bemonovoid.playqd.datasource.jdbc.entity.AlbumEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.ArtistEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.SongEntity;
import com.bemonovoid.playqd.datasource.jdbc.projection.CountProjection;
import com.bemonovoid.playqd.datasource.jdbc.repository.AlbumRepository;
import com.bemonovoid.playqd.datasource.jdbc.repository.ArtistRepository;
import com.bemonovoid.playqd.datasource.jdbc.repository.SongRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class ArtistDaoImpl implements ArtistDao {

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;
    private final PlaybackHistoryDao playbackHistoryDao;

    private final JdbcTemplate jdbcTemplate;

    ArtistDaoImpl(ArtistRepository artistRepository,
                  AlbumRepository albumRepository,
                  SongRepository songRepository,
                  PlaybackHistoryDao playbackHistoryDao,
                  JdbcTemplate jdbcTemplate) {
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.songRepository = songRepository;
        this.playbackHistoryDao = playbackHistoryDao;
        this.jdbcTemplate = jdbcTemplate;
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
    public boolean update(Artist artist) {
        log.info("Updating artist with id='{}'.", artist.getId());
        ArtistEntity entity = artistRepository.findOne(artist.getId());
        boolean hasUpdates = false;
        if (shouldUpdate(entity.getName(), artist.getName())) {
            entity.setName(artist.getName());
            entity.setSimpleName(EntityNameHelper.toLookUpName(artist.getName()));
            hasUpdates = true;
        }
        if (shouldUpdate(entity.getCountry(), artist.getCountry())) {
            entity.setCountry(artist.getCountry());
            hasUpdates = true;
        }
        if (shouldUpdate(entity.getMbArtistId(), artist.getMbArtistId())) {
            entity.setMbArtistId(artist.getMbArtistId());
            hasUpdates = true;
        }
        if (hasUpdates) {
            artistRepository.save(entity);
            log.info("Artist with id='{} updated.'", artist.getId());
        }
        return hasUpdates;
    }

    public void move(long fromArtistId, long toArtistId) {
        log.info("Moving artist id={} to artist id={}", fromArtistId, toArtistId);

        ArtistEntity artistFrom = artistRepository.findOne(fromArtistId);
        ArtistEntity artistTo = artistRepository.findOne(toArtistId);

        List<AlbumEntity> albumsFrom = artistFrom.getAlbums();
        List<SongEntity> songsFrom = artistFrom.getAlbums().stream()
                .peek(albumEntity -> albumEntity.setArtist(artistTo))
                .flatMap(album -> album.getSongs().stream()
                        .peek(songEntity -> songEntity.setArtist(artistTo)))
                .collect(Collectors.toList());

        albumRepository.saveAll(albumsFrom);
        songRepository.saveAll(songsFrom);

        log.info("Moving completed. Moved {} album(s) and {} song(s)", albumsFrom.size(), songsFrom.size());

        jdbcTemplate.update("DELETE FROM ARTIST a WHERE a.ID = ?", fromArtistId);

        log.info("Old artist id={} removed.", fromArtistId);
    }

    private boolean shouldUpdate(String oldVal, String newVal) {
        return newVal != null && !newVal.isBlank() && !newVal.equalsIgnoreCase(oldVal);
    }

}
