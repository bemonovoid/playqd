package com.bemonovoid.playqd.online.search;

import java.util.Optional;

public interface ArtworkOnlineSearchService {

    Optional<ArtworkSearchResult> search(ArtworkSearchFilter criteria);

}
