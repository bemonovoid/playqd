package com.bemonovoid.playqd.core.dao;

import java.util.Map;

import com.bemonovoid.playqd.core.model.PlaybackHistoryArtist;

public interface PlaybackHistoryDao {

    Map<Long, PlaybackHistoryArtist> getArtistPlaybackHistory();

}
