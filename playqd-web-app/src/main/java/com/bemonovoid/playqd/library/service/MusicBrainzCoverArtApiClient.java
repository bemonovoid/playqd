package com.bemonovoid.playqd.library.service;

import java.util.Optional;

import com.bemonovoid.playqd.library.model.musicbrainz.MBCoverArtQueryResponse;

public interface MusicBrainzCoverArtApiClient {

    Optional<MBCoverArtQueryResponse> getCoverArt(String mbReleaseId);
}
