package com.bemonovoid.playqd.core.service;

import java.util.Optional;

import com.bemonovoid.playqd.core.model.ArtworkOnlineSearchResult;
import com.bemonovoid.playqd.core.model.query.ArtworkOnlineSearchQuery;

public interface ArtworkSearchService {

    Optional<ArtworkOnlineSearchResult> search(ArtworkOnlineSearchQuery onlineSearchQuery);

}
