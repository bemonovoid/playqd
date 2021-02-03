package com.bemonovoid.playqd.core.service;

import java.util.Map;

import com.bemonovoid.playqd.core.model.PlaybackHistoryArtist;

public interface PlaybackHistoryService {

    void updatePlaybackHistoryWithSongEnded(long songId);

    Map<Long, PlaybackHistoryArtist> getArtistPlaybackHistory();
}
