package com.bemonovoid.playqd.core.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MusicBrainzTagValues {

    private String mbArtistId;
    private String mbArtistCountry;
    private String mbReleaseId;
}
