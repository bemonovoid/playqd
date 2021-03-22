package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.bemonovoid.playqd.core.dao.AlbumDao;
import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.MoveResult;
import com.bemonovoid.playqd.core.model.SortDirection;
import com.bemonovoid.playqd.core.model.pageable.PageableRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;
import com.bemonovoid.playqd.core.model.pageable.SortBy;
import com.bemonovoid.playqd.core.model.pageable.SortRequest;
import com.bemonovoid.playqd.datasource.jdbc.entity.AlbumEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.SongEntity;
import com.bemonovoid.playqd.datasource.jdbc.projection.GenreProjection;
import com.bemonovoid.playqd.datasource.jdbc.repository.AlbumRepository;
import com.bemonovoid.playqd.datasource.jdbc.repository.SongRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class AlbumDaoImpl implements AlbumDao {

    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;
    private final JdbcTemplate jdbcTemplate;
    AlbumDaoImpl(JdbcTemplate jdbcTemplate, AlbumRepository albumRepository, SongRepository songRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.albumRepository = albumRepository;
        this.songRepository = songRepository;
    }

    @Override
    public Optional<Album> findOne(String id) {
        return albumRepository.findById(UUID.fromString(id)).map(AlbumHelper::fromEntity);
    }

    @Override
    public Album getOne(String id) {
        return AlbumHelper.fromEntity(albumRepository.findOne(UUID.fromString(id)));
    }

    @Override
    public PageableResult<Album> getAlbums(PageableRequest pageableRequest) {
        Sort sort = getAlbumSort(pageableRequest.getSort());
        PageRequest pageRequest = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize(), sort);
        return new PageableResultWrapper<>(albumRepository.findAll(pageRequest).map(AlbumHelper::fromEntity));
    }

    @Override
    public PageableResult<Album> getGenreAlbums(String genre, PageableRequest pageableRequest) {
        Sort sort = getAlbumSort(pageableRequest.getSort());
        PageRequest pageRequest = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize(), sort);
        return new PageableResultWrapper<>(albumRepository.findByGenreEquals(genre, pageRequest)
                .map(AlbumHelper::fromEntity));
    }

    @Override
    public PageableResult<Album> getArtistAlbums(String artistId, PageableRequest pageableRequest) {
        Sort sort = getAlbumSort(pageableRequest.getSort());
        PageRequest pageRequest = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize(), sort);
        return new PageableResultWrapper<>(
                albumRepository.findByArtistId(UUID.fromString(artistId), pageRequest).map(AlbumHelper::fromEntity));
    }

    @Override
    public PageableResult<Album> getAlbumsWithNameContaining(String albumName, PageableRequest pageableRequest) {
        Sort sort = Sort.unsorted();
        if (pageableRequest.getSort() != null) {
            sort = Sort.sort(AlbumEntity.class).by(AlbumEntity::getName).ascending();
            if (SortDirection.DESC == pageableRequest.getSort().getDirection()) {
                sort = sort.descending();
            }
        }
        PageRequest pageRequest = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize(), sort);
        return new PageableResultWrapper<>(albumRepository.findByNameContaining(
                albumName, pageRequest).map(AlbumHelper::fromEntity));
    }

    @Override
    public PageableResult<Album> getArtistAlbumsWithNameContaining(String artistId,
                                                                   String albumName,
                                                                   PageableRequest pageableRequest) {
        Sort sort = Sort.unsorted();
        if (pageableRequest.getSort() != null) {
            sort = Sort.sort(AlbumEntity.class).by(AlbumEntity::getName).ascending();
            if (SortDirection.DESC == pageableRequest.getSort().getDirection()) {
                sort = sort.descending();
            }
        }
        PageRequest pageRequest = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize(), sort);
        return new PageableResultWrapper<>(albumRepository.findByArtistIdAndNameContaining(
                UUID.fromString(artistId), albumName, pageRequest).map(AlbumHelper::fromEntity));
    }

    @Override
    public PageableResult<String> getGenres(PageableRequest pageableRequest) {
        Sort sort = Sort.sort(AlbumEntity.class).by(AlbumEntity::getGenre).ascending();
        if (pageableRequest.getSort() != null && SortDirection.DESC == pageableRequest.getSort().getDirection()) {
            sort = sort.descending();
        }
        PageRequest pageRequest = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize(), sort);
        return new PageableResultWrapper<>(albumRepository.findDistinctGenre(pageRequest));
    }

    @Override
    public PageableResult<String> getGenresWithNameContaining(String genre, PageableRequest pageableRequest) {
        Sort sort = Sort.sort(AlbumEntity.class).by(AlbumEntity::getGenre).ascending();
        if (pageableRequest.getSort() != null && SortDirection.DESC == pageableRequest.getSort().getDirection()) {
            sort = sort.descending();
        }
        PageRequest pageRequest = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize(), sort);
        Page<String> genrePage = albumRepository.findDistinctByGenreIgnoreCaseContaining(
                genre.toUpperCase(), pageRequest).map(GenreProjection::getGenre);
        return new PageableResultWrapper<>(genrePage);
    }

    @Override
    public Album save(Album album) {
        AlbumEntity albumEntity = albumRepository.save(AlbumHelper.toEntity(album));
        return AlbumHelper.fromEntity(albumEntity);
    }

    @Override
    public void updateAlbum(Album album) {

        log.info("Updating album with id='{}'.", album.getId());

        AlbumEntity entity = albumRepository.findOne(UUID.fromString(album.getId()));

        if (shouldUpdate(entity.getName(), album.getName())) {
            entity.setName(album.getName());
        }
        if (shouldUpdate(entity.getDate(), album.getDate())) {
            entity.setDate(album.getDate());
        }
        if (shouldUpdate(entity.getGenre(), album.getGenre())) {
            entity.setGenre(album.getGenre());
        }

        albumRepository.save(entity);

        log.info("Updating album with id='{} completed.'", album.getId());
    }

    @Override
    public void saveAlbumImage(String albumId, byte[] binaryData) {
        albumRepository.findById(UUID.fromString(albumId)).ifPresent(entity -> {
            entity.setImage(binaryData);
            albumRepository.save(entity);
        });
    }

    @Override
    public MoveResult move(String albumIdFrom, String albumIdTo) {
        log.info("Moving album id={} to album id={}", albumIdFrom, albumIdTo);

        AlbumEntity albumFrom = albumRepository.findOne(UUID.fromString(albumIdFrom));
        AlbumEntity albumTo = albumRepository.findOne(UUID.fromString(albumIdTo));

        albumFrom.getSongs().forEach(albumSongFromEntity -> albumSongFromEntity.setAlbum(albumTo));

        List<SongEntity> movedSongs = songRepository.saveAll(albumFrom.getSongs());

        log.info("Moved {} song(s). Move completed.", movedSongs.size());

        jdbcTemplate.update("DELETE FROM ALBUM a WHERE a.ID = ?", albumIdFrom);

        log.info("Old album id={} removed.", albumIdFrom);

        return MoveResult.builder()
                .newAlbum(AlbumHelper.fromEntity(albumTo))
                .movedSongFiles(movedSongs.stream().map(SongEntity::getFileLocation).collect(Collectors.toList()))
                .build();
    }

    private boolean shouldUpdate(String oldVal, String newVal) {
        return newVal != null && !newVal.isBlank() && !newVal.equalsIgnoreCase(oldVal);
    }

    private Sort getAlbumSort(SortRequest sortRequest) {
        Sort sort = Sort.unsorted();
        if (sortRequest != null) {
            sort = Sort.sort(AlbumEntity.class).by(AlbumEntity::getName).ascending();
            if (SortBy.DATE == sortRequest.getSortBy()) {
                sort = Sort.sort(AlbumEntity.class).by(AlbumEntity::getDate);
            }
            if (SortDirection.DESC == sortRequest.getDirection()) {
                sort = sort.descending();
            }
        }
        return sort;
    }
}
