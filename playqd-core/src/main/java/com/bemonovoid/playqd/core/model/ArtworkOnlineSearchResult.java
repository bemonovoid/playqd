package com.bemonovoid.playqd.core.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ArtworkOnlineSearchResult {

    private String imageUrl;
    private String mbArtistId;
    private String mbArtistCountry;
    private String mbReleaseId;

}
