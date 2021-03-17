package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.bemonovoid.playqd.core.dao.SongDao;
import com.bemonovoid.playqd.core.exception.PlayqdEntityNotFoundException;
import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.model.pageable.PageableRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;
import com.bemonovoid.playqd.core.service.SecurityService;
import com.bemonovoid.playqd.datasource.jdbc.entity.AlbumEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.SongEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.SongPreferencesEntity;
import com.bemonovoid.playqd.datasource.jdbc.projection.FileLocationProjection;
import com.bemonovoid.playqd.datasource.jdbc.repository.AlbumRepository;
import com.bemonovoid.playqd.datasource.jdbc.repository.SongPreferencesRepository;
import com.bemonovoid.playqd.datasource.jdbc.repository.SongRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
class SongDaoImpl implements SongDao {

    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final SongPreferencesRepository songPreferencesRepository;

    SongDaoImpl(SongRepository songRepository,
                AlbumRepository albumRepository,
                SongPreferencesRepository songPreferencesRepository) {
        this.songRepository = songRepository;
        this.albumRepository = albumRepository;
        this.songPreferencesRepository = songPreferencesRepository;
    }

    @Override
    public Song getOne(long id) {
        return songRepository.findById(id)
                .map(SongHelper::fromEntity)
                .orElseThrow(() -> new PlayqdEntityNotFoundException(id, "song"));
    }

    @Override
    public String getSongFileLocation(long songId) {
        return songRepository.findSongFileLocation(songId)
                .orElseThrow(() -> new PlayqdEntityNotFoundException(songId, "song"));
    }

    @Override
    public PageableResult<Song> getSongs(PageableRequest pageableRequest) {
        Sort sort = Sort.sort(SongEntity.class).by(SongEntity::getName).ascending();
        PageRequest pageRequest = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize(), sort);
        return new PageableResultWrapper<>(songRepository.findAll(pageRequest).map(SongHelper::fromEntity));
    }

    @Override
    public List<Song> getAlbumSongs(long albumId) {
        Map<Long, SongPreferencesEntity> songsPreferences =
                songPreferencesRepository.findAlbumSongsPreferences(albumId, SecurityService.getCurrentUserName());
        Sort sort = Sort.sort(SongEntity.class).by(SongEntity::getTrackId).ascending();
        return SongHelper.fromAlbumSongEntities(songRepository.findByAlbumId(albumId, sort), songsPreferences);
    }

    @Override
    public PageableResult<Song> getFavoriteSongs(PageableRequest pageableRequest) {
        PageRequest pageRequest = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize());
        return new PageableResultWrapper<>(songRepository.findFavoriteSongs(pageRequest).map(SongHelper::fromEntity));
    }

    @Override
    public PageableResult<Song> getMostPlayedSongs(PageableRequest pageableRequest) {
        PageRequest pageRequest = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize());
        return new PageableResultWrapper<>(songRepository.findMostPlayedSongs(pageRequest).map(SongHelper::fromEntity));
    }

    @Override
    public PageableResult<Song> getRecentlyPlayedSongs(PageableRequest pageableRequest) {
        PageRequest pageRequest = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize());
        return new PageableResultWrapper<>(
                songRepository.findRecentlyPlayedSongs(pageRequest).map(SongHelper::fromEntity));
    }

    @Override
    public PageableResult<Song> getRecentlyAddedSongs(PageableRequest pageableRequest) {
        Sort sort = Sort.sort(SongEntity.class).by(SongEntity::getCreatedDate).descending();
        PageRequest pageRequest = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize(), sort);
        return new PageableResultWrapper<>(songRepository.findAll(pageRequest).map(SongHelper::fromEntity));
    }

    @Override
    public PageableResult<Song> getSongsWithNameContaining(String name, PageableRequest pageableRequest) {
        Sort sort = Sort.sort(SongEntity.class).by(SongEntity::getName).ascending();
        PageRequest pageRequest = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize(), sort);
        return new PageableResultWrapper<>(
                songRepository.findWithNameContaining(name, pageRequest).map(SongHelper::fromEntity));
    }

    @Override
    public List<String> getArtistSongsFileLocations(long artistId) {
        return songRepository.findArtistSongsFileLocations(artistId);
    }

    @Override
    public void updateSong(Song song) {
        log.info("Updating album with id='{}'.", song.getId());

        SongEntity entity = songRepository.findOne(song.getId());

        if (shouldUpdate(entity.getName(), song.getName())) {
            entity.setName(song.getName());
        }
        if (shouldUpdate(entity.getComment(), song.getComment())) {
            entity.setComment(song.getComment());
        }
        if (shouldUpdate(entity.getLyrics(), song.getLyrics())) {
            entity.setLyrics(song.getLyrics());
        }
        if (shouldUpdate(entity.getTrackId(), song.getTrackId())) {
            entity.setTrackId(Integer.parseInt(song.getTrackId()));
        }

        songRepository.save(entity);

        log.info("Updating album with id='{} completed.'", song.getId());
    }

    @Override
    public List<String> getAlbumSongsFileLocations(long albumId) {
        return songRepository.findAlbumSongsFileLocations(albumId);
    }

    @Override
    public Optional<String> getAnyAlbumSongFileLocation(long albumId) {
        return songRepository.findFirstByAlbumId(albumId).map(FileLocationProjection::getFileLocation);
    }

    @Override
    @Transactional
    public void updateFavoriteFlag(long songId, boolean isFavorite) {
        SongEntity songEntity = songRepository.findOne(songId);
        if (songEntity.isFavorite() == isFavorite) {
            return;
        }
        songEntity.setFavorite(isFavorite);
        songRepository.save(songEntity);
    }

    @Override
    @Transactional
    public void updatePlayCount(long songId) {
        SongEntity songEntity = songRepository.findOne(songId);
        songEntity.setPlayCount(songEntity.getPlayCount() + 1);
        songRepository.save(songEntity);
    }

    @Override
    public Song moveSong(long songId, long albumId) {
        SongEntity songEntity = songRepository.findOne(songId);
        AlbumEntity albumEntity = albumRepository.findOne(albumId);
        songEntity.setAlbum(albumEntity);
        SongEntity movedSong = songRepository.save(songEntity);
        return SongHelper.fromEntity(movedSong);
    }

    private boolean shouldUpdate(Object oldVal, String newVal) {
        if (newVal != null) {
            if (oldVal == null) {
                return true;
            }
            return !newVal.isBlank() && !newVal.equalsIgnoreCase(oldVal.toString());
        }
        return false;
    }

}
