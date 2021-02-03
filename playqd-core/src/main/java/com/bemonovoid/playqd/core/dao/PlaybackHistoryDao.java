package com.bemonovoid.playqd.core.dao;

import java.util.Map;

import com.bemonovoid.playqd.core.model.PlaybackHistoryArtist;

public interface PlaybackHistoryDao {

    Long save(long songId);

    Map<Long, PlaybackHistoryArtist> getArtistPlaybackHistory();
}
