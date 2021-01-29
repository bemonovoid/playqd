package com.bemonovoid.playqd.remote.service.musicbrainz.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MBQueryContext {

    private String mbArtistId;
    private String mbArtistCountry;
    private String albumName;
}
