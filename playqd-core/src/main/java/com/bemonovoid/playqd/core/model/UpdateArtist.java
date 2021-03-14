package com.bemonovoid.playqd.core.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
public class UpdateArtist {

    private String name;
    private String country;
    private boolean updateAudioTags;
}
