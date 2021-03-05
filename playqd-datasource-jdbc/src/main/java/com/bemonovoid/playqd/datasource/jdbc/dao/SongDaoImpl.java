package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.bemonovoid.playqd.core.dao.SongDao;
import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.service.SecurityService;
import com.bemonovoid.playqd.datasource.jdbc.entity.PlaybackInfoEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.SongEntity;
import com.bemonovoid.playqd.datasource.jdbc.projection.FileLocationProjection;
import com.bemonovoid.playqd.datasource.jdbc.repository.PlaybackInfoRepository;
import com.bemonovoid.playqd.datasource.jdbc.repository.SongRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
class SongDaoImpl implements SongDao {

    private final SongRepository songRepository;
    private final PlaybackInfoRepository playbackInfoRepository;

    SongDaoImpl(SongRepository songRepository, PlaybackInfoRepository playbackInfoRepository) {
        this.songRepository = songRepository;
        this.playbackInfoRepository = playbackInfoRepository;
    }

    @Override
    public Optional<Song> getOne(long id) {
        return songRepository.findById(id).map(SongHelper::fromEntity);
    }

    @Override
    public List<Song> getAlbumSongs(long albumId) {
        return songRepository.findAllByAlbumId(albumId).stream().map(SongHelper::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<String> getArtistSongsFileLocations(long artistId) {
        return songRepository.findByArtistId(artistId).stream()
                .map(FileLocationProjection::getFileLocation)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAlbumSongsFileLocations(long albumId) {
        return getAlbumSongs(albumId).stream().map(Song::getFileLocation).collect(Collectors.toList());
    }

    @Override
    public Optional<String> getAnyAlbumSongFileLocation(long albumId) {
        return songRepository.findFirstByAlbumId(albumId).map(FileLocationProjection::getFileLocation);
    }

    @Override
    public List<Song> getTopPlayedSongs(int pageSize) {
        return songRepository.findTopPlayedSongs(SecurityService.getCurrentUser(), PageRequest.of(0, pageSize)).stream()
                .map(SongHelper::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Song> getRecentlyPlayedSongs(int pageSize) {
        String username = SecurityService.getCurrentUser();
        return songRepository.findRecentlyPlayedSongs(username, PageRequest.of(0, pageSize)).stream()
                .map(SongHelper::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Song> getRecentlyAdded(int pageSize) {
        Sort sort = Sort.sort(SongEntity.class).by(SongEntity::getCreatedDate).descending();
        return songRepository.findAll(PageRequest.of(0, pageSize, sort)).stream()
                .map(SongHelper::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<Song> getFavoriteSongs(int pageSize) {
        String username = SecurityService.getCurrentUser();
        return songRepository.findFavoriteSongs(username, PageRequest.of(0, pageSize)).stream()
                .map(SongHelper::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void updateFavoriteFlag(long songId, boolean isFavorite) {
        Optional<PlaybackInfoEntity> playbackInfoEntityOpt =
                playbackInfoRepository.findBySongIdAndCreatedBy(songId, SecurityService.getCurrentUser());

        if (playbackInfoEntityOpt.isEmpty() && !isFavorite) {
            return;
        }

        PlaybackInfoEntity playbackInfoEntity = playbackInfoEntityOpt.orElseGet(() -> {
                SongEntity songEntity = songRepository.findOne(songId);
                PlaybackInfoEntity entity = new PlaybackInfoEntity();
                entity.setSong(songEntity);
                return entity;
            });
        playbackInfoEntity.setFavorite(isFavorite);
        playbackInfoRepository.save(playbackInfoEntity);
    }

    @Override
    public void updatePlayCount(long songId) {
        PlaybackInfoEntity playbackInfoEntity =
                playbackInfoRepository.findBySongIdAndCreatedBy(songId, SecurityService.getCurrentUser())
                        .orElseGet(() -> {
                            SongEntity songEntity = songRepository.findOne(songId);
                            PlaybackInfoEntity entity = new PlaybackInfoEntity();
                            entity.setSong(songEntity);
                            return entity;
                });
        playbackInfoEntity.setPlayCount(playbackInfoEntity.getPlayCount() + 1);
        playbackInfoRepository.save(playbackInfoEntity);
    }

    @Override
    public void setShowAlbumSongNameAsFileName(long albumId) {
        songRepository.setShowFileNameAsSongName(albumId);
    }
}
