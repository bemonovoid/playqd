package com.bemonovoid.playqd.library.model.musicbrainz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MBArtworkInfo {

    private String mbArtistId;
    private String mbReleaseId;
    private String imageUrl;
}
