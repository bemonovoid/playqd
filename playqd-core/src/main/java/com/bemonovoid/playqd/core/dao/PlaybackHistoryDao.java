package com.bemonovoid.playqd.core.dao;

import java.util.Map;

import com.bemonovoid.playqd.core.model.PlaybackHistoryArtist;
import com.bemonovoid.playqd.core.model.PlaybackHistorySong;

public interface PlaybackHistoryDao {

    Long save(long songId);

    Map<Long, PlaybackHistoryArtist> getArtistPlaybackHistory();

    Map<Long, PlaybackHistorySong> findTopRecentlyPlayedSongs(int pageSize);
}
