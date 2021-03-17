package com.bemonovoid.playqd.core.dao;

import java.util.Map;

import com.bemonovoid.playqd.core.model.PlaybackInfoArtist;

public interface PlaybackHistoryDao {

    Map<Long, PlaybackInfoArtist> getArtistPlaybackInfo();

}
