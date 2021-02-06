package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.util.Map;
import java.util.stream.Collectors;

import com.bemonovoid.playqd.core.dao.PlaybackHistoryDao;
import com.bemonovoid.playqd.core.model.PlaybackHistoryArtist;
import com.bemonovoid.playqd.core.model.PlaybackHistorySong;
import com.bemonovoid.playqd.datasource.jdbc.entity.PlaybackHistoryEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.SongEntity;
import com.bemonovoid.playqd.datasource.jdbc.projection.PlaybackHistoryArtistProjection;
import com.bemonovoid.playqd.datasource.jdbc.projection.PlaybackHistorySongProjection;
import com.bemonovoid.playqd.datasource.jdbc.repository.PlaybackHistoryRepository;
import com.bemonovoid.playqd.datasource.jdbc.repository.SongRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
class PlaybackHistoryDaoImpl implements PlaybackHistoryDao {

    private final SongRepository songRepository;
    private final PlaybackHistoryRepository playbackHistoryRepository;

    PlaybackHistoryDaoImpl(SongRepository songRepository, PlaybackHistoryRepository playbackHistoryRepository) {
        this.songRepository = songRepository;
        this.playbackHistoryRepository = playbackHistoryRepository;
    }

    @Override
    public Long save(long songId) {
        SongEntity songEntity = songRepository.getOne(songId);
        PlaybackHistoryEntity playbackHistoryEntity = new PlaybackHistoryEntity();
        playbackHistoryEntity.setSong(songEntity);
        return playbackHistoryRepository.save(playbackHistoryEntity).getId();
    }

    @Override
    public Map<Long, PlaybackHistoryArtist> getArtistPlaybackHistory() {
        return playbackHistoryRepository.groupByArtistPlaybackHistory().stream()
                .collect(Collectors.toMap(
                        PlaybackHistoryArtistProjection::getArtistId,
                        projection -> new PlaybackHistoryArtist(
                                projection.getArtistId(),
                                projection.getPlayCount(),
                                projection.getMostRecentPlayDateTime().toString())));
    }

    @Override
    public Map<Long, PlaybackHistorySong> findTopRecentlyPlayedSongs(int pageSize) {
        return playbackHistoryRepository.findTopRecentlyPlayedSongs(PageRequest.of(0, pageSize)).stream()
                .collect(Collectors.toMap(PlaybackHistorySongProjection::getSongId,
                        projection -> new PlaybackHistorySong(
                                projection.getSongId(),
                                projection.getPlayCount(),
                                projection.getMostRecentPlayDateTime().toString()
                        )));
    }
}
