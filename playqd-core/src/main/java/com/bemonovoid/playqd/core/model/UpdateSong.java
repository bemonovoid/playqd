package com.bemonovoid.playqd.core.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
public class UpdateSong {

    private String name;
    private String comment;
    private String lyrics;
    private String trackId;
    private boolean updateAudioTags;
}
