package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.bemonovoid.playqd.core.dao.SongDao;
import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.datasource.jdbc.repository.SongRepository;
import org.springframework.stereotype.Component;

@Component
class SongDaoImpl implements SongDao {

    private final SongRepository repository;

    SongDaoImpl(SongRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Song> getOne(long id) {
        return repository.findById(id).map(SongHelper::fromEntity);
    }

    @Override
    public List<Song> getArtistSongs(long artistId) {
        return repository.findAllByArtistId(artistId).stream().map(SongHelper::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<Song> getAlbumSongs(long albumId) {
        return repository.findAllByAlbumId(albumId).stream().map(SongHelper::fromEntity).collect(Collectors.toList());
    }

    @Override
    public Optional<Song> getFirstSongInAlbum(long albumId) {
        return repository.findFirstByAlbumId(albumId).map(SongHelper::fromEntity);
    }
}
