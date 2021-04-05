package com.bemonovoid.playqd.core.dao;

import com.bemonovoid.playqd.core.model.ScannerLog;
import com.bemonovoid.playqd.core.model.pageable.PageableRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;

public interface ScannerLogDao {

    String save(ScannerLog scannerLog);

    PageableResult<ScannerLog> getLogs(PageableRequest pageableRequest);
}
