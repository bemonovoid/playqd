package com.bemonovoid.playqd.library.service;

import com.bemonovoid.playqd.library.model.AlbumArtwork;
import com.bemonovoid.playqd.library.model.query.ArtworkQuery;

public interface ArtworkService {

    AlbumArtwork get(ArtworkQuery query);
}
