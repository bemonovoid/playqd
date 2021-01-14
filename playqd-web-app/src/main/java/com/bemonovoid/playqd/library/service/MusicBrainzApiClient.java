package com.bemonovoid.playqd.library.service;

import java.util.Optional;

import com.bemonovoid.playqd.library.model.musicbrainz.MBArtistQueryResponse;
import com.bemonovoid.playqd.library.model.musicbrainz.MBArtistReleasesQueryResponse;

public interface MusicBrainzApiClient {

    Optional<MBArtistQueryResponse> getArtist(String artistName);

    Optional<MBArtistReleasesQueryResponse> getReleases(String mbArtistId);
}
