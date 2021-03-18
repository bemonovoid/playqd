package com.bemonovoid.playqd.core.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SongPreferences {

    private String songId;
    private boolean songNameAsFileName;

}
