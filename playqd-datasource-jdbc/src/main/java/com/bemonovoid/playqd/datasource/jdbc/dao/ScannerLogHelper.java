package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.time.Duration;

import com.bemonovoid.playqd.core.model.ScannerLog;
import com.bemonovoid.playqd.datasource.jdbc.entity.MusicDatabaseUpdateLogEntity;

class ScannerLogHelper {

    public static ScannerLog fromEntity(MusicDatabaseUpdateLogEntity entity) {
        return ScannerLog.builder()
                .status(entity.getScanStatus())
                .statusDetails(entity.getStatusDetails())
                .deleteAllBeforeScan(entity.isDeleteAllBeforeScan())
                .indexedFilesMissing(entity.getIndexedFilesMissing())
                .filesIndexed(entity.getFilesIndexed())
                .scanDirectory(entity.getScanDirectory())
                .scanDuration(Duration.ofMillis(entity.getScanDurationInMillis()))
                .scanDate(entity.getCreatedDate())
                .build();
    }
}
