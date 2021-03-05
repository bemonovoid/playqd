package com.bemonovoid.playqd.core.dao;

import java.util.Map;

import com.bemonovoid.playqd.core.model.PlaybackHistoryArtist;
import com.bemonovoid.playqd.core.model.PlaybackInfo;

public interface PlaybackHistoryDao {

    Map<Long, PlaybackHistoryArtist> getArtistPlaybackHistory();

}
