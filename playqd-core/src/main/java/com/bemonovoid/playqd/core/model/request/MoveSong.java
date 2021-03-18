package com.bemonovoid.playqd.core.model.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
public class MoveSong {

    private String albumIdTo;
    private boolean updateAudioTags;
}
