package com.bemonovoid.playqd.datasource.jdbc.dao;

import com.bemonovoid.playqd.core.model.PlaybackHistoryArtist;
import com.bemonovoid.playqd.datasource.jdbc.projection.CountProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArtistMetadata {

    private final CountProjection counts;
    private final PlaybackHistoryArtist playbackHistory;
}
