package com.bemonovoid.playqd.library.service;

import java.util.Optional;

import com.bemonovoid.playqd.library.model.musicbrainz.MBArtworkInfo;

public interface MusicBrainzService {

    Optional<MBArtworkInfo> getArtworkInfo(String artistName, String albumName);

    Optional<MBArtworkInfo> getArtworkInfoForMbArtist(String mbArtistId, String albumName);
}
