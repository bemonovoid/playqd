package com.bemonovoid.playqd.core.service;

import com.bemonovoid.playqd.core.model.ScannerLog;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;

public interface ScannerLogService {

    String save(ScannerLog scannerLog);

    PageableResult<ScannerLog> getScannerLogs(int page, int pageSize);

}
