package com.bemonovoid.playqd.core.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DirectoryScanLog {

    private int numberOfSongsAdded;
    private String directory;
    private long durationInSeconds;
    private boolean cleanAllApplied;
    private DirectoryScanStatus status;
}
