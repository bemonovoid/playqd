package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.bemonovoid.playqd.core.dao.AlbumDao;
import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.datasource.jdbc.entity.AlbumEntity;
import com.bemonovoid.playqd.datasource.jdbc.repository.AlbumRepository;
import com.bemonovoid.playqd.datasource.jdbc.repository.SongRepository;
import lombok.extern.slf4j.Slf4j;
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
    public Optional<Album> findOne(long id) {
        return albumRepository.findById(id).map(AlbumHelper::fromEntity);
    }

    @Override
    public Album getOne(long id) {
        return AlbumHelper.fromEntity(albumRepository.findOne(id));
    }

    @Override
    public List<Album> getAll() {
        return albumRepository.findAll().stream().map(AlbumHelper::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<String> getGenres() {
        return albumRepository.findDistinctGenre();
    }

    @Override
    public List<Album> getGenreAlbums(String genre) {
        return albumRepository.findAllByGenreEquals(genre).stream()
                .map(AlbumHelper::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<Album> getArtistAlbums(long artistId) {
        return albumRepository.findAllByArtistId(artistId).stream()
                .map(AlbumHelper::fromEntity).collect(Collectors.toList());
    }

    @Override
    public Album save(Album album) {
        AlbumEntity albumEntity = albumRepository.save(AlbumHelper.toEntity(album));
        return AlbumHelper.fromEntity(albumEntity);
    }

    @Override
    public void updateAlbum(Album album) {

        log.info("Updating album with id='{}'.", album.getId());

        AlbumEntity entity = albumRepository.findOne(album.getId());

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
    public void setArtworkBinary(long albumId, byte[] binaryData) {
        albumRepository.findById(albumId).ifPresent(entity -> {
//            entity.setArtworkBinary(binaryData);
            albumRepository.save(entity);
        });
    }

    @Override
    public void move(long albumIdFrom, Long albumIdTo) {
        log.info("Moving album id={} to album id={}", albumIdFrom, albumIdTo);

        AlbumEntity albumFrom = albumRepository.findOne(albumIdFrom);
        AlbumEntity albumTo = albumRepository.findOne(albumIdTo);

        albumFrom.getSongs().forEach(albumSongFromEntity -> albumSongFromEntity.setAlbum(albumTo));

        songRepository.saveAll(albumFrom.getSongs());

        log.info("Moving completed. Moved {} song(s)", albumFrom.getSongs().size());

        jdbcTemplate.update("DELETE FROM ALBUM a WHERE a.ID = ?", albumIdFrom);

        log.info("Old album id={} removed.", albumIdFrom);

    }

    private boolean shouldUpdate(String oldVal, String newVal) {
        return newVal != null && !newVal.isBlank() && !newVal.equalsIgnoreCase(oldVal);
    }
}
