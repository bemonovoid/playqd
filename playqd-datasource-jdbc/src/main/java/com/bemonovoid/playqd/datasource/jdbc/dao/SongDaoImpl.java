package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.dao.SongDao;
import com.bemonovoid.playqd.core.exception.PlayqdEntityNotFoundException;
import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.model.pageable.PageableRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;
import com.bemonovoid.playqd.core.service.SecurityService;
import com.bemonovoid.playqd.datasource.jdbc.entity.PlaybackInfoEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.SongEntity;
import com.bemonovoid.playqd.datasource.jdbc.projection.FileLocationProjection;
import com.bemonovoid.playqd.datasource.jdbc.repository.PlaybackInfoRepository;
import com.bemonovoid.playqd.datasource.jdbc.repository.SongRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class SongDaoImpl implements SongDao {

    private final SongRepository songRepository;
    private final PlaybackInfoRepository playbackInfoRepository;

    SongDaoImpl(SongRepository songRepository, PlaybackInfoRepository playbackInfoRepository) {
        this.songRepository = songRepository;
        this.playbackInfoRepository = playbackInfoRepository;
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
    public PageableResult<Song> getFavoriteSongs(PageableRequest pageableRequest) {
        String username = SecurityService.getCurrentUserName();
        PageRequest pageRequest = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize());
        return new PageableResultWrapper<>(
                songRepository.findFavoriteSongs(username, pageRequest).map(SongHelper::fromEntity));
    }

    @Override
    public PageableResult<Song> getMostPlayedSongs(PageableRequest pageableRequest) {
        String username = SecurityService.getCurrentUserName();
        PageRequest pageRequest = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize());
        return new PageableResultWrapper<>(
                songRepository.findMostPlayedSongs(username, pageRequest).map(SongHelper::fromEntity));
    }

    @Override
    public PageableResult<Song> getRecentlyPlayedSongs(PageableRequest pageableRequest) {
        String username = SecurityService.getCurrentUserName();
        PageRequest pageRequest = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize());
        return new PageableResultWrapper<>(
                songRepository.findRecentlyPlayedSongs(username, pageRequest).map(SongHelper::fromEntity));
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
    public List<Song> getAlbumSongs(long albumId) {
        return SongHelper.fromAlbumSongEntities(songRepository.findByAlbumId(albumId));
    }

    @Override
    public List<String> getArtistSongsFileLocations(long artistId) {
        return songRepository.findArtistSongsFileLocations(artistId);
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

        PlaybackInfoEntity playbackInfoEntity = songEntity.getPlaybackInfo().stream()
                .filter(p -> p.getCreatedBy().equals(SecurityService.getCurrentUserName()))
                .findFirst()
                .orElseGet(() -> {
                    PlaybackInfoEntity entity = new PlaybackInfoEntity();
                    entity.setSong(songEntity);
                    return entity;
                });
        if (playbackInfoEntity.isFavorite() == isFavorite) {
            return;
        }
        playbackInfoEntity.setFavorite(isFavorite);
        playbackInfoRepository.save(playbackInfoEntity);
    }

    @Override
    @Transactional
    public void updatePlayCount(long songId) {
        String username = SecurityService.getCurrentUserName();
        SongEntity songEntity = songRepository.findOne(songId);
        PlaybackInfoEntity playbackInfoEntity = songEntity.getPlaybackInfo().stream()
                .filter(p -> p.getCreatedBy().equals(username))
                .findFirst()
                .orElseGet(() -> {
                    PlaybackInfoEntity entity = new PlaybackInfoEntity();
                    entity.setSong(songEntity);
                    return entity;
                });
        playbackInfoEntity.setPlayCount(playbackInfoEntity.getPlayCount() + 1);
        playbackInfoRepository.save(playbackInfoEntity);
    }

}
