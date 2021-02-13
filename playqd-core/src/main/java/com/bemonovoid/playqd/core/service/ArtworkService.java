package com.bemonovoid.playqd.core.service;

import java.util.Optional;

import com.bemonovoid.playqd.core.model.Artwork;
import com.bemonovoid.playqd.core.model.query.ArtworkLocalSearchQuery;

public interface ArtworkService {

    Artwork getArtworkFromLibrary(ArtworkLocalSearchQuery localQuery);

    Optional<String> getArtworkOnline(ArtworkLocalSearchQuery localQuery);

    void updateAlbumArtwork(long albumId, String resourceUrl);
}