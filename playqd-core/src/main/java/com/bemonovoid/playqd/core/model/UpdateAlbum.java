package com.bemonovoid.playqd.core.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
public class UpdateAlbum {

    private String name;
    private String genre;
    private String date;
    private String artworkSrc;
    private boolean overrideSongNameWithFileName;
}
