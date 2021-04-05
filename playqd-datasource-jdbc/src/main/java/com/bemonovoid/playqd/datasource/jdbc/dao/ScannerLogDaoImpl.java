package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.util.UUID;

import com.bemonovoid.playqd.core.dao.ScannerLogDao;
import com.bemonovoid.playqd.core.model.ScannerLog;
import com.bemonovoid.playqd.core.model.pageable.PageableRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;
import com.bemonovoid.playqd.datasource.jdbc.entity.MusicDatabaseUpdateLogEntity;
import com.bemonovoid.playqd.datasource.jdbc.repository.MusicDatabaseUpdateLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class ScannerLogDaoImpl implements ScannerLogDao {

    private final MusicDatabaseUpdateLogRepository musicDatabaseUpdateLogRepository;

    ScannerLogDaoImpl(MusicDatabaseUpdateLogRepository musicDatabaseUpdateLogRepository) {
        this.musicDatabaseUpdateLogRepository = musicDatabaseUpdateLogRepository;
    }

    @Override
    @Transactional
    public String save(ScannerLog scannerLog) {
        MusicDatabaseUpdateLogEntity entity = new MusicDatabaseUpdateLogEntity();
        entity.setUUID(scannerLog.getId());
        if (scannerLog.getId() != null) {
            entity = musicDatabaseUpdateLogRepository.findOne(entity.getId());
        }
        entity.setScanStatus(scannerLog.getStatus());
        entity.setScanDirectory(scannerLog.getScanDirectory());
        entity.setDeleteAllBeforeScan(scannerLog.isDeleteAllBeforeScan());
        entity.setFilesIndexed(scannerLog.getFilesIndexed());
        entity.setScanDurationInMillis(scannerLog.getScanDuration().toMillis());
        entity.setIndexedFilesMissing(scannerLog.getIndexedFilesMissing());
        entity.setStatusDetails(scannerLog.getStatusDetails());
        MusicDatabaseUpdateLogEntity savedEntity = musicDatabaseUpdateLogRepository.save(entity);
        return savedEntity.getUUID();
    }

    @Override
    public PageableResult<ScannerLog> getLogs(PageableRequest pageableRequest) {
        Sort sort = Sort.sort(MusicDatabaseUpdateLogEntity.class)
                .by(MusicDatabaseUpdateLogEntity::getCreatedDate).descending();
        Pageable pageable = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize(), sort);
        Page<ScannerLog> page =
                musicDatabaseUpdateLogRepository.findAll(pageable).map(ScannerLogHelper::fromEntity);
        return new PageableResultWrapper<>(page);
    }

}
