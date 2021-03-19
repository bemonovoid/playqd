package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

    private final SongHelper songHelper;
    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final SongPreferencesRepository songPreferencesRepository;

    SongDaoImpl(SongHelper songHelper,
                SongRepository songRepository,
                AlbumRepository albumRepository,
                SongPreferencesRepository songPreferencesRepository) {
        this.songHelper = songHelper;
        this.songRepository = songRepository;
        this.albumRepository = albumRepository;
        this.songPreferencesRepository = songPreferencesRepository;
    }

    @Override
    public Song getOne(String songId) {
        return songRepository.findById(UUID.fromString(songId))
                .map(songHelper::fromEntity)
                .orElseThrow(() -> new PlayqdEntityNotFoundException(songId, "song"));
    }

    @Override
    public String getSongFileLocation(String songId) {
        return songRepository.findSongFileLocation(UUID.fromString(songId))
                .orElseThrow(() -> new PlayqdEntityNotFoundException(songId, "song"));
    }

    @Override
    public PageableResult<Song> getSongs(PageableRequest pageableRequest) {
        Sort sort = Sort.sort(SongEntity.class).by(SongEntity::getName).ascending();
        PageRequest pageRequest = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize(), sort);
        return new PageableResultWrapper<>(songRepository.findAll(pageRequest).map(songHelper::fromEntity));
    }

    @Override
    public List<Song> getAlbumSongs(String albumId) {
        Map<String, SongPreferencesEntity> songsPreferences = songPreferencesRepository.findAlbumSongsPreferences(
                UUID.fromString(albumId), SecurityService.getCurrentUserName());

        Sort sort = Sort.sort(SongEntity.class).by(SongEntity::getTrackId).ascending();

        return songHelper.fromAlbumSongEntities(songRepository.findByAlbumId(
                UUID.fromString(albumId), sort), songsPreferences);
    }

    @Override
    public PageableResult<Song> getFavoriteSongs(PageableRequest pageableRequest) {
        PageRequest pageRequest = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize());
        return new PageableResultWrapper<>(songRepository.findFavoriteSongs(pageRequest).map(songHelper::fromEntity));
    }

    @Override
    public PageableResult<Song> getMostPlayedSongs(PageableRequest pageableRequest) {
        PageRequest pageRequest = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize());
        return new PageableResultWrapper<>(songRepository.findMostPlayedSongs(pageRequest).map(songHelper::fromEntity));
    }

    @Override
    public PageableResult<Song> getRecentlyPlayedSongs(PageableRequest pageableRequest) {
        PageRequest pageRequest = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize());
        return new PageableResultWrapper<>(
                songRepository.findRecentlyPlayedSongs(pageRequest).map(songHelper::fromEntity));
    }

    @Override
    public PageableResult<Song> getRecentlyAddedSongs(PageableRequest pageableRequest) {
        Sort sort = Sort.sort(SongEntity.class).by(SongEntity::getCreatedDate).descending();
        PageRequest pageRequest = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize(), sort);
        return new PageableResultWrapper<>(songRepository.findAll(pageRequest).map(songHelper::fromEntity));
    }

    @Override
    public PageableResult<Song> getSongsWithNameContaining(String name, PageableRequest pageableRequest) {
        Sort sort = Sort.sort(SongEntity.class).by(SongEntity::getName).ascending();
        PageRequest pageRequest = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize(), sort);
        return new PageableResultWrapper<>(
                songRepository.findWithNameContaining(name, pageRequest).map(songHelper::fromEntity));
    }

    @Override
    public List<String> getArtistSongsFileLocations(String artistId) {
        return songRepository.findArtistSongsFileLocations(UUID.fromString(artistId));
    }

    @Override
    public void updateSong(Song song) {
        log.info("Updating album with id='{}'.", song.getId());

        SongEntity entity = songRepository.findOne(UUID.fromString(song.getId()));

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
    public List<String> getAlbumSongsFileLocations(String albumId) {
        return songRepository.findAlbumSongsFileLocations(UUID.fromString(albumId));
    }

    @Override
    public Optional<String> getAnyAlbumSongFileLocation(String albumId) {
        return songRepository.findFirstByAlbumId(UUID.fromString(albumId)).map(FileLocationProjection::getFileLocation);
    }

    @Override
    @Transactional
    public void updateFavoriteFlag(String songId, boolean isFavorite) {
        SongEntity songEntity = songRepository.findOne(UUID.fromString(songId));
        if (songEntity.isFavorite() == isFavorite) {
            return;
        }
        songEntity.setFavorite(isFavorite);
        songRepository.save(songEntity);
    }

    @Override
    @Transactional
    public void updatePlayCount(String songId) {
        SongEntity songEntity = songRepository.findOne(UUID.fromString(songId));
        songEntity.setPlayCount(songEntity.getPlayCount() + 1);
        songRepository.save(songEntity);
    }

    @Override
    public Song moveSong(String songId, String albumId) {
        SongEntity songEntity = songRepository.findOne(UUID.fromString(songId));
        AlbumEntity albumEntity = albumRepository.findOne(UUID.fromString(albumId));
        songEntity.setAlbum(albumEntity);
        SongEntity movedSong = songRepository.save(songEntity);
        return songHelper.fromEntity(movedSong);
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
