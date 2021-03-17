package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import com.bemonovoid.playqd.core.dao.PlaybackHistoryDao;
import com.bemonovoid.playqd.core.model.PlaybackInfoArtist;
import com.bemonovoid.playqd.core.model.PlaybackInfoSong;
import com.bemonovoid.playqd.core.service.SecurityService;
import com.bemonovoid.playqd.datasource.jdbc.projection.PlaybackInfoArtistProjection;
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
    public Map<Long, PlaybackInfoArtist> getArtistPlaybackInfo() {
        return playbackInfoRepository.playbackInfoGroupedByArtist(SecurityService.getCurrentUserName()).stream()
                .collect(Collectors.toMap(
                        PlaybackInfoArtistProjection::getArtistId,
                        projection -> new PlaybackInfoArtist(
                                projection.getArtistId(),
                                projection.getPlayCount(),
                                projection.getMostRecentPlayDateTime().toString())));
    }

    public Map<Long, PlaybackInfoSong> getSongsPlaybackInfo() {
        return Collections.emptyMap();
    }
}
