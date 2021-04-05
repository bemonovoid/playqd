package com.bemonovoid.playqd.core.service.impl;

import com.bemonovoid.playqd.core.dao.ScannerLogDao;
import com.bemonovoid.playqd.core.model.ScannerLog;
import com.bemonovoid.playqd.core.model.pageable.PageableRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;
import com.bemonovoid.playqd.core.model.pageable.SortRequest;
import com.bemonovoid.playqd.core.service.ScannerLogService;
import org.springframework.stereotype.Service;

@Service
class ScannerLogServiceImpl implements ScannerLogService {

    private final ScannerLogDao scannerLogDao;

    ScannerLogServiceImpl(ScannerLogDao scannerLogDao) {
        this.scannerLogDao = scannerLogDao;
    }

    @Override
    public String save(ScannerLog scannerLog) {
        return scannerLogDao.save(scannerLog);
    }

    @Override
    public PageableResult<ScannerLog> getScannerLogs(int page, int pageSize) {
        PageableRequest pageableRequest = new PageableRequest(page, pageSize, SortRequest.builder().build());
        return scannerLogDao.getLogs(pageableRequest);
    }
}
