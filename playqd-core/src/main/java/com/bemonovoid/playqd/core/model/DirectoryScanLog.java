package com.bemonovoid.playqd.core.model;

import java.time.Duration;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DirectoryScanLog {

    private int numberOfSongsAdded;
    private String directory;
    private Duration duration;
    private boolean cleanAllApplied;
    private DirectoryScanStatus status;
}
