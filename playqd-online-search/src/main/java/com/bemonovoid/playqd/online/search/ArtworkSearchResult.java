package com.bemonovoid.playqd.online.search;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ArtworkSearchResult {

    private String imageUrl;
    private String mbArtistId;
    private String mbArtistCountry;
    private String mbReleaseId;

}
