package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.bemonovoid.playqd.core.dao.ArtistDao;
import com.bemonovoid.playqd.core.exception.PlayqdEntityNotFoundException;
import com.bemonovoid.playqd.core.helpers.EntityNameHelper;
import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.BasicArtist;
import com.bemonovoid.playqd.core.model.MoveResult;
import com.bemonovoid.playqd.core.model.pageable.FindArtistsRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;
import com.bemonovoid.playqd.core.service.SecurityService;
import com.bemonovoid.playqd.datasource.jdbc.entity.AlbumEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.ArtistEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.SongEntity;
import com.bemonovoid.playqd.datasource.jdbc.projection.CountProjection;
import com.bemonovoid.playqd.datasource.jdbc.repository.AlbumRepository;
import com.bemonovoid.playqd.datasource.jdbc.repository.ArtistRepository;
import com.bemonovoid.playqd.datasource.jdbc.repository.PlaybackInfoRepository;
import com.bemonovoid.playqd.datasource.jdbc.repository.SongRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
class ArtistDaoImpl implements ArtistDao {

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;
    private final PlaybackInfoRepository playbackInfoRepository;

    private final JdbcTemplate jdbcTemplate;

    ArtistDaoImpl(ArtistRepository artistRepository,
                  AlbumRepository albumRepository,
                  SongRepository songRepository,
                  PlaybackInfoRepository playbackInfoRepository,
                  JdbcTemplate jdbcTemplate) {
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.songRepository = songRepository;
        this.playbackInfoRepository = playbackInfoRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Artist getOne(long id) {
        return artistRepository.findById(id)
                .map(ArtistHelper::fromEntity)
                .orElseThrow(() -> new PlayqdEntityNotFoundException(id, "artist"));
    }

    @Override
    public List<BasicArtist> getAllBasicArtists() {
        return artistRepository.findAllBasicArtists().stream()
                .map(projection -> new BasicArtist(projection.getId(), projection.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public PageableResult<Artist> getAll(FindArtistsRequest request) {
        String username = SecurityService.getCurrentUserName();
        Map<Long, CountProjection> counts = songRepository.getArtistAlbumSongCount();
        FindArtistsRequest.ArtistSortBy sortBy = request.getSortBy();
        if (sortBy == null) {
            sortBy = FindArtistsRequest.ArtistSortBy.NAME;
        }
        switch (sortBy) {
            case RECENTLY_PLAYED:
                return new PageableResultWrapper<>(getRecentlyPlayedArtists(username, counts, request));
            case MOST_PLAYED:
                return new PageableResultWrapper<>(getMostPlayedArtists(username, counts, request));
            case TOTAL_ALBUMS:
            case TOTAL_SONGS:
            default:
                return new PageableResultWrapper<>(getArtistsOrderedByName(counts, request));
        }
    }

    @Override
    public void setSpotifyArtistId(long artistId, String spotifyId) {
        ArtistEntity entity = artistRepository.findOne(artistId);
        entity.setSpotifyArtistId(spotifyId);
        artistRepository.save(entity);
    }

    @Override
    public void update(Artist artist) {
        log.info("Updating artist with id='{}'.", artist.getId());
        ArtistEntity entity = artistRepository.findOne(artist.getId());
        if (shouldUpdate(entity.getName(), artist.getName())) {
            entity.setName(artist.getName());
            entity.setSimpleName(EntityNameHelper.toLookUpName(artist.getName()));
        }
        if (shouldUpdate(entity.getCountry(), artist.getCountry())) {
            entity.setCountry(artist.getCountry());
        }

        artistRepository.save(entity);

        log.info("Artist with id='{} updated.'", artist.getId());
    }

    public MoveResult move(long fromArtistId, long toArtistId) {
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
        List<SongEntity> movedSongs = songRepository.saveAll(songsFrom);

        log.info("Moving completed. Moved {} album(s) and {} song(s)", albumsFrom.size(), songsFrom.size());

        jdbcTemplate.update("DELETE FROM ARTIST a WHERE a.ID = ?", fromArtistId);

        log.info("Old artist id={} removed.", fromArtistId);

        return MoveResult.builder()
                .newArtist(ArtistHelper.fromEntity(artistTo))
                .movedSongFiles(movedSongs.stream().map(SongEntity::getFileLocation).collect(Collectors.toList()))
                .build();
    }

    private boolean shouldUpdate(String oldVal, String newVal) {
        return newVal != null && !newVal.isBlank() && !newVal.equalsIgnoreCase(oldVal);
    }

    private Page<Artist> getArtistsOrderedByName(Map<Long, CountProjection> counts, FindArtistsRequest request) {
        Sort sort = Sort.by(Sort.Direction.fromString(request.getDirection().name()), ArtistEntity.FLD_NAME);
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize(), sort);
        if (StringUtils.hasText(request.getName())) {
            String nameContaining = EntityNameHelper.toLookUpName(request.getName());
            return artistRepository.findBySimpleNameContaining(nameContaining, pageRequest)
                    .map(artistEntity -> ArtistHelper.fromEntity(artistEntity, counts.get(artistEntity.getId())));
        }
        return artistRepository.findAll(pageRequest)
                .map(artistEntity -> ArtistHelper.fromEntity(artistEntity, counts.get(artistEntity.getId())));
    }

    private Page<Artist> getRecentlyPlayedArtists(String username,
                                                  Map<Long, CountProjection> counts,
                                                  FindArtistsRequest request) {
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());
        return artistRepository.findRecentlyPlayedArtists(username, pageRequest)
                .map(artistRepository::findOne)
                .map(artistEntity -> ArtistHelper.fromEntity(artistEntity, counts.get(artistEntity.getId())));
    }

    private Page<Artist> getMostPlayedArtists(String username,
                                              Map<Long, CountProjection> counts,
                                              FindArtistsRequest request) {
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());
        return artistRepository.findMostPlayedArtists(username, pageRequest)
                .map(artistRepository::findOne)
                .map(artistEntity -> ArtistHelper.fromEntity(artistEntity, counts.get(artistEntity.getId())));
    }

}
