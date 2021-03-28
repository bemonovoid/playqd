package com.bemonovoid.playqd.datasource.jdbc.dao;

import com.bemonovoid.playqd.core.dao.DirectoryScanLogDao;
import com.bemonovoid.playqd.core.model.ScannerLog;
import com.bemonovoid.playqd.datasource.jdbc.entity.MusicDatabaseUpdateLogEntity;
import com.bemonovoid.playqd.datasource.jdbc.repository.MusicDatabaseUpdateLogRepository;
import org.springframework.stereotype.Component;

@Component
class DirectoryScanLogDaoImpl implements DirectoryScanLogDao {

    private final MusicDatabaseUpdateLogRepository musicDatabaseUpdateLogRepository;

    DirectoryScanLogDaoImpl(MusicDatabaseUpdateLogRepository musicDatabaseUpdateLogRepository) {
        this.musicDatabaseUpdateLogRepository = musicDatabaseUpdateLogRepository;
    }

    @Override
    public void save(ScannerLog scannerLog) {
        MusicDatabaseUpdateLogEntity entity = new MusicDatabaseUpdateLogEntity();
        entity.setScanStatus(scannerLog.getStatus());
        entity.setScanDirectory(scannerLog.getScanDirectory());
        entity.setDeleteAllBeforeScan(scannerLog.isDeleteAllBeforeScan());
        entity.setFilesIndexed(scannerLog.getFilesIndexed());
        entity.setScanDurationInMillis(scannerLog.getScanDuration().toMillis());
        entity.setIndexedFilesMissing(scannerLog.getIndexedFilesMissing());
        musicDatabaseUpdateLogRepository.save(entity);
    }

}
