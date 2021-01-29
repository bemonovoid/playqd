package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.bemonovoid.playqd.core.dao.AlbumDao;
import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.datasource.jdbc.entity.AlbumEntity;
import com.bemonovoid.playqd.datasource.jdbc.repository.AlbumRepository;
import org.springframework.stereotype.Component;

@Component
class AlbumDaoImpl implements AlbumDao {

    private final AlbumRepository repository;

    AlbumDaoImpl(AlbumRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Album> getOne(long id) {
        return repository.findById(id).map(AlbumHelper::fromEntity);
    }

    @Override
    public List<Album> getAll() {
        return repository.findAll().stream().map(AlbumHelper::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<String> getGenres() {
        return repository.findDistinctGenre();
    }

    @Override
    public List<Album> getAllByGenre(String genre) {
        return repository.findAllByGenreEquals(genre).stream()
                .map(AlbumHelper::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<Album> getArtistAlbums(long artistId) {
        return repository.findAllByArtistId(artistId).stream()
                .map(AlbumHelper::fromEntity).collect(Collectors.toList());
    }

    @Override
    public void updateArtwork(Long albumId, String mbReleaseId, byte[] binary) {
        repository.findById(albumId).ifPresent(albumEntity -> {
            if (mbReleaseId != null) {
                albumEntity.setMbReleaseId(mbReleaseId);
            }
            if (binary != null) {
                albumEntity.setArtworkBinary(binary);
            }
            repository.save(albumEntity);
        });
    }

    @Override
    public Album save(Album album) {
        AlbumEntity albumEntity = repository.save(AlbumHelper.toEntity(album));
        return AlbumHelper.fromEntity(albumEntity);
    }
}
