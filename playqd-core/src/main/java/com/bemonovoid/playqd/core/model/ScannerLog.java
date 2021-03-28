package com.bemonovoid.playqd.core.model;

import java.time.Duration;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScannerLog {

    private int filesIndexed;
    private int indexedFilesMissing;
    private String scanDirectory;
    private Duration scanDuration;
    private DirectoryScanStatus status;
    private boolean deleteAllBeforeScan;
}
