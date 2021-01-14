package com.bemonovoid.playqd.library.service;

import java.util.Optional;

import com.bemonovoid.playqd.core.ArtworkLocalQuery;
import com.bemonovoid.playqd.online.search.ArtworkBinary;

public interface ArtworkService {

    ArtworkBinary getBinaryFromLocalLibrary(ArtworkLocalQuery localQuery);

    Optional<String> searchOnline(ArtworkLocalQuery localQuery);
}
