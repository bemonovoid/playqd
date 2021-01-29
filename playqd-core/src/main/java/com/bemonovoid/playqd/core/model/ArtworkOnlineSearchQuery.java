package com.bemonovoid.playqd.core.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class ArtworkOnlineSearchQuery {

    private String artistName;
    private String albumName;
    private String mbArtistId;

}
