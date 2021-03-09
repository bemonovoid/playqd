package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.util.Map;
import java.util.stream.Collectors;

import com.bemonovoid.playqd.core.dao.PlaybackHistoryDao;
import com.bemonovoid.playqd.core.model.PlaybackHistoryArtist;
import com.bemonovoid.playqd.core.service.SecurityService;
import com.bemonovoid.playqd.datasource.jdbc.projection.PlaybackHistoryArtistProjection;
import com.bemonovoid.playqd.datasource.jdbc.repository.PlaybackInfoRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
class PlaybackHistoryDaoImpl implements PlaybackHistoryDao {

    private final PlaybackInfoRepository playbackInfoRepository;

    PlaybackHistoryDaoImpl(PlaybackInfoRepository playbackInfoRepository) {
        this.playbackInfoRepository = playbackInfoRepository;
    }



    @Override
    public Map<Long, PlaybackHistoryArtist> getArtistPlaybackHistory() {
        return playbackInfoRepository.groupByArtistPlaybackHistory(SecurityService.getCurrentUserName()).stream()
                .collect(Collectors.toMap(
                        PlaybackHistoryArtistProjection::getArtistId,
                        projection -> new PlaybackHistoryArtist(
                                projection.getArtistId(),
                                projection.getPlayCount(),
                                projection.getMostRecentPlayDateTime().toString())));
    }


}
