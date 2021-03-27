package com.bemonovoid.playqd.core.service.impl;

import com.bemonovoid.playqd.core.dao.DirectoryScanLogDao;
import com.bemonovoid.playqd.core.model.ScannerLog;
import com.bemonovoid.playqd.core.service.DirectoryScanLogService;
import org.springframework.stereotype.Service;

@Service
class DirectoryScanLogServiceImpl implements DirectoryScanLogService {

    private final DirectoryScanLogDao directoryScanLogDao;

    DirectoryScanLogServiceImpl(DirectoryScanLogDao directoryScanLogDao) {
        this.directoryScanLogDao = directoryScanLogDao;
    }

    @Override
    public void save(ScannerLog scannerLog) {
        directoryScanLogDao.save(scannerLog);
    }
}
