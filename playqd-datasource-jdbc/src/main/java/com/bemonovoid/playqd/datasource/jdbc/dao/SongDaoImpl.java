package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.bemonovoid.playqd.core.dao.PlaybackHistoryDao;
import com.bemonovoid.playqd.core.dao.SongDao;
import com.bemonovoid.playqd.core.model.PlaybackHistorySong;
import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.datasource.jdbc.repository.SongRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
class SongDaoImpl implements SongDao {

    private final SongRepository repository;
    private final PlaybackHistoryDao playbackHistoryDao;

    SongDaoImpl(SongRepository repository, PlaybackHistoryDao playbackHistoryDao) {
        this.repository = repository;
        this.playbackHistoryDao = playbackHistoryDao;
    }

    @Override
    public Optional<Song> getOne(long id) {
        return repository.findById(id).map(SongHelper::fromEntity);
    }

    @Override
    public List<Song> getAlbumSongs(long albumId) {
        return repository.findAllByAlbumId(albumId).stream().map(SongHelper::fromEntity).collect(Collectors.toList());
    }

    @Override
    public Optional<Song> getFirstSongInAlbum(long albumId) {
        return repository.findFirstByAlbumId(albumId).map(SongHelper::fromEntity);
    }

    @Override
    public List<Song> getTopPlayedSongs(int pageSize) {
        Map<Long, PlaybackHistorySong> topPlayedSongs = playbackHistoryDao.findTopPlayedSongs(pageSize);
        return repository.findAllById(topPlayedSongs.keySet()).stream()
                .map(songEntity -> SongHelper.fromEntity(songEntity, topPlayedSongs.get(songEntity.getId())))
                .sorted((s1, s2) -> Integer.compare(
                        s2.getPlaybackHistory().getPlayCount(), s1.getPlaybackHistory().getPlayCount()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Song> getTopRecentlyPlayedSongs(int pageSize) {
        Map<Long, PlaybackHistorySong> recentlyPlayedSongs = playbackHistoryDao.findTopRecentlyPlayedSongs(pageSize);
        return repository.findAllById(recentlyPlayedSongs.keySet()).stream()
                .map(songEntity -> SongHelper.fromEntity(songEntity, recentlyPlayedSongs.get(songEntity.getId())))
                .collect(Collectors.toList());
    }
}
