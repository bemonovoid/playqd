package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.bemonovoid.playqd.core.dao.ArtistDao;
import com.bemonovoid.playqd.core.exception.PlayqdEntityNotFoundException;
import com.bemonovoid.playqd.core.helpers.EntityNameHelper;
import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.MoveResult;
import com.bemonovoid.playqd.core.model.SortDirection;
import com.bemonovoid.playqd.core.model.pageable.PageableRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;
import com.bemonovoid.playqd.datasource.jdbc.entity.AlbumEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.ArtistEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.SongEntity;
import com.bemonovoid.playqd.datasource.jdbc.projection.CountProjection;
import com.bemonovoid.playqd.datasource.jdbc.repository.AlbumRepository;
import com.bemonovoid.playqd.datasource.jdbc.repository.ArtistRepository;
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

    private final JdbcTemplate jdbcTemplate;

    ArtistDaoImpl(ArtistRepository artistRepository,
                  AlbumRepository albumRepository,
                  SongRepository songRepository,
                  JdbcTemplate jdbcTemplate) {
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.songRepository = songRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Artist getOne(String id) {
        return artistRepository.findById(UUID.fromString(id))
                .map(ArtistHelper::fromEntity)
                .orElseThrow(() -> new PlayqdEntityNotFoundException(id, "artist"));
    }

    @Override
    public PageableResult<Artist> getBasicArtists(PageableRequest pageableRequest) {
        return getArtists(
                pageableRequest, artistRepository::findAllBasicArtists, ArtistHelper::fromProjection);
    }

    @Override
    public PageableResult<Artist> getArtists(PageableRequest pageableRequest) {
        return getArtists(
                pageableRequest,
                artistRepository::findAll,
                artistEntity -> ArtistHelper.fromEntity(artistEntity, getCounts().get(artistEntity.getId())));
    }

    @Override
    public PageableResult<Artist> getArtistsWithNameContaining(String name, PageableRequest pageableRequest) {
        Sort sort = Sort.sort(ArtistEntity.class).by(ArtistEntity::getName).ascending();
        if (pageableRequest.getSort() != null && SortDirection.DESC == pageableRequest.getSort().getDirection()) {
            sort = sort.descending();
        }
        PageRequest pageRequest = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize(), sort);
        return new PageableResultWrapper<>(artistRepository.findByArtistNameContaining(name, pageRequest)
                .map(artistEntity -> ArtistHelper.fromEntity(artistEntity, getCounts().get(artistEntity.getId()))));
    }

    @Override
    public PageableResult<Artist> getRecentlyPlayedArtists(PageableRequest pageableRequest) {
        PageRequest pageRequest = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize());
        return new PageableResultWrapper<>(artistRepository.findRecentlyPlayedArtists(pageRequest)
                .map(artistRepository::findOne)
                .map(artistEntity -> ArtistHelper.fromEntity(artistEntity, getCounts().get(artistEntity.getId()))));
    }

    @Override
    public PageableResult<Artist> getMostPlayedArtists(PageableRequest pageableRequest) {
        PageRequest pageRequest = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize());
        return new PageableResultWrapper<>(artistRepository.findMostPlayedArtists(pageRequest)
                .map(artistRepository::findOne)
                .map(artistEntity -> ArtistHelper.fromEntity(artistEntity, getCounts().get(artistEntity.getId()))));
    }

    @Override
    public void update(Artist artist) {
        log.info("Updating artist with id='{}'.", artist.getId());
        ArtistEntity entity = artistRepository.findOne(UUID.fromString(artist.getId()));

        if (propertyChanged(entity.getName(), artist.getName())) {
            entity.setName(artist.getName());
            entity.setSimpleName(EntityNameHelper.toLookUpName(artist.getName()));
        }
        if (propertyChanged(entity.getCountry(), artist.getCountry())) {
            entity.setCountry(artist.getCountry());
        }
        if (propertyChanged(entity.getSpotifyArtistId(), artist.getSpotifyId())) {
            entity.setSpotifyArtistId(artist.getSpotifyId());
        }
        if (propertyChanged(entity.getSpotifyArtistName(), artist.getSpotifyName())) {
            entity.setSpotifyArtistName(artist.getSpotifyName());
        }

        artistRepository.save(entity);

        log.info("Artist with id='{} updated.'", artist.getId());
    }

    @Override
    public MoveResult move(String fromArtistId, String toArtistId) {
        log.info("Moving artist id={} to artist id={}", fromArtistId, toArtistId);

        ArtistEntity artistFrom = artistRepository.findOne(UUID.fromString(fromArtistId));
        ArtistEntity artistTo = artistRepository.findOne(UUID.fromString(toArtistId));

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

    private <T> PageableResult<Artist> getArtists(PageableRequest pageableRequest,
                                                  Function<PageRequest, Page<T>> query,
                                                  Function<T, Artist> mapper) {
        Sort sort = Sort.sort(ArtistEntity.class).by(ArtistEntity::getName).ascending();
        if (pageableRequest.getSort() != null && SortDirection.DESC == pageableRequest.getSort().getDirection()) {
            sort = sort.descending();
        }
        PageRequest pageRequest = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize(), sort);
        return new PageableResultWrapper<>(query.apply(pageRequest).map(mapper));
    }

    private boolean propertyChanged(String oldVal, String newVal) {
        if (!StringUtils.hasText(newVal)) {
            return false;
        }
        if (StringUtils.hasText(oldVal)) {
            return !oldVal.equals(newVal);
        }
        return true;
    }

    private Map<UUID, CountProjection> getCounts() {
        return songRepository.getArtistAlbumSongCount();
    }

}
