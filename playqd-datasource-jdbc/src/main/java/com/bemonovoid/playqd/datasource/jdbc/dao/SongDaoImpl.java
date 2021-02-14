package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.bemonovoid.playqd.core.dao.PlaybackHistoryDao;
import com.bemonovoid.playqd.core.dao.SongDao;
import com.bemonovoid.playqd.core.model.PlaybackHistorySong;
import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.datasource.jdbc.entity.FavoriteSongEntity;
import com.bemonovoid.playqd.datasource.jdbc.projection.FileLocationProjection;
import com.bemonovoid.playqd.datasource.jdbc.repository.FavoriteSongRepository;
import com.bemonovoid.playqd.datasource.jdbc.repository.SongRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
class SongDaoImpl implements SongDao {

    private final SongRepository songRepository;
    private final PlaybackHistoryDao playbackHistoryDao;
    private final FavoriteSongRepository favoriteSongRepository;

    SongDaoImpl(SongRepository songRepository,
                PlaybackHistoryDao playbackHistoryDao,
                FavoriteSongRepository favoriteSongRepository) {
        this.songRepository = songRepository;
        this.playbackHistoryDao = playbackHistoryDao;
        this.favoriteSongRepository = favoriteSongRepository;
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
    public Optional<Song> getFirstSongInAlbum(long albumId) {
        return songRepository.findFirstByAlbumId(albumId).map(SongHelper::fromEntity);
    }

    @Override
    public List<Song> getTopPlayedSongs(int pageSize) {
        Map<Long, PlaybackHistorySong> topPlayedSongs = playbackHistoryDao.findTopPlayedSongs(pageSize);
        return songRepository.findAllById(topPlayedSongs.keySet()).stream()
                .map(songEntity -> SongHelper.fromEntity(songEntity, topPlayedSongs.get(songEntity.getId())))
                .sorted((s1, s2) -> Integer.compare(
                        s2.getPlaybackHistory().getPlayCount(), s1.getPlaybackHistory().getPlayCount()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Song> getTopRecentlyPlayedSongs(int pageSize) {
        Map<Long, PlaybackHistorySong> recentlyPlayedSongs = playbackHistoryDao.findTopRecentlyPlayedSongs(pageSize);
        return songRepository.findAllById(recentlyPlayedSongs.keySet()).stream()
                .map(songEntity -> SongHelper.fromEntity(songEntity, recentlyPlayedSongs.get(songEntity.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<Song> getFavoriteSongs(int pageSize) {
        return songRepository.findFavoriteSongs(PageRequest.of(0, pageSize)).stream()
                .map(SongHelper::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void updateFavoriteStatus(long songId) {
        if (favoriteSongRepository.existsBySongId(songId)) {
            favoriteSongRepository.deleteBySongId(songId);
        } else {
            FavoriteSongEntity favoriteSongEntity = new FavoriteSongEntity();
            favoriteSongEntity.setSongId(songId);
            favoriteSongRepository.save(favoriteSongEntity);
        }
    }

    @Override
    public void setShowAlbumSongNameAsFileName(long albumId) {
        songRepository.setShowFileNameAsSongName(albumId);
    }
}
