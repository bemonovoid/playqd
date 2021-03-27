package com.bemonovoid.playqd.core.model;

import java.time.Duration;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScannerLog {

    private int filesIndexed;
    private String directory;
    private Duration duration;
    private boolean deleteAllBeforeScan;
    private DirectoryScanStatus status;
}
