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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
class AlbumDaoImpl implements AlbumDao {

    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;

    AlbumDaoImpl(AlbumRepository albumRepository, SongRepository songRepository) {
        this.albumRepository = albumRepository;
        this.songRepository = songRepository;
    }

    @Override
    public Optional<Album> getOne(long id) {
        return albumRepository.findById(id).map(AlbumHelper::fromEntity);
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
    public List<Album> getAllByGenre(String genre) {
        return albumRepository.findAllByGenreEquals(genre).stream()
                .map(AlbumHelper::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<Album> getArtistAlbums(long artistId) {
        return albumRepository.findAllByArtistId(artistId).stream()
                .map(AlbumHelper::fromEntity).collect(Collectors.toList());
    }

    @Override
    public void updateArtwork(Long albumId, String mbReleaseId, byte[] binary) {
        albumRepository.findById(albumId).ifPresent(albumEntity -> {
            if (mbReleaseId != null) {
                albumEntity.setMbReleaseId(mbReleaseId);
            }
            if (binary != null) {
                albumEntity.setArtworkBinary(binary);
            }
            albumRepository.save(albumEntity);
        });
    }

    @Override
    public Album save(Album album) {
        AlbumEntity albumEntity = albumRepository.save(AlbumHelper.toEntity(album));
        return AlbumHelper.fromEntity(albumEntity);
    }

    @Override
    @Transactional
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
        if (album.getArtwork() != null && album.getArtwork().getBinary() != null) {
            entity.setArtworkBinary(album.getArtwork().getBinary());
        }

        albumRepository.save(entity);
        songRepository.overrideAlbumSongsNameWithFileName(entity.getId());

        log.info("Updating album with id='{} completed.'", album.getId());
    }

    private boolean shouldUpdate(String oldVal, String newVal) {
        return newVal != null && !newVal.isBlank() && !newVal.equalsIgnoreCase(oldVal);
    }
}
