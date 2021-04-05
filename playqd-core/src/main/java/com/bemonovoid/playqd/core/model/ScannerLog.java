package com.bemonovoid.playqd.core.model;

import java.time.Duration;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScannerLog {

    private int filesIndexed;
    private int indexedFilesMissing;
    private String scanDirectory;
    private DirectoryScanStatus status;
    private String statusDetails;
    private boolean deleteAllBeforeScan;

    @JsonFormat(pattern = "dd-MM-YYYY hh:mm")
    private LocalDateTime scanDate;

    @JsonIgnore
    private Duration scanDuration;

    @JsonIgnore
    private String id;

    @JsonProperty("durationInMillis")
    public long getDurationInMillis() {
        return getScanDuration().toMillis();
    }
}
