package com.bemonovoid.playqd.online.search;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class ArtworkSearchFilter {

    private String artistName;
    private String albumName;
    private String mbArtistId;

}
