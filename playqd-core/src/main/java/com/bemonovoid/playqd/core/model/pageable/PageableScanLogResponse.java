package com.bemonovoid.playqd.core.model.pageable;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.bemonovoid.playqd.core.model.ScannerLog;

public class PageableScanLogResponse extends PageableResponse<ScannerLog> {

    public PageableScanLogResponse(PageableResult<ScannerLog> result) {
        super(result);
    }

    @JsonProperty("scans")
    public List<ScannerLog> getScans() {
        return getContent();
    }
}
