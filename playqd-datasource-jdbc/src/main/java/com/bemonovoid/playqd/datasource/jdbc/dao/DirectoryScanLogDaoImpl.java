package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

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
        entity.setStatus(scannerLog.getStatus());
        entity.setDirectory(scannerLog.getDirectory());
        entity.setCleanAllApplied(scannerLog.isDeleteAllBeforeScan());
        entity.setNumberOfSongsAdded(scannerLog.getFilesIndexed());

        Duration duration = scannerLog.getDuration();
        String durationString = "0";
        if (duration.getSeconds() > 0) {
            durationString = duration.getSeconds() + " second(s)";
        } else if (entity.getNumberOfSongsAdded() > 0) {
            durationString = TimeUnit.MILLISECONDS.convert(duration) + " milliseconds";
        }
        entity.setDuration(durationString);
        musicDatabaseUpdateLogRepository.save(entity);
    }

}
