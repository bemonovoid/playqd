package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.bemonovoid.playqd.core.dao.DirectoryScanLogDao;
import com.bemonovoid.playqd.core.model.DirectoryScanLog;
import com.bemonovoid.playqd.datasource.jdbc.entity.DirectoryScanLogEntity;
import com.bemonovoid.playqd.datasource.jdbc.repository.DirectoryScanLogRepository;
import org.springframework.stereotype.Component;

@Component
class DirectoryScanLogDaoImpl implements DirectoryScanLogDao {

    private final DirectoryScanLogRepository directoryScanLogRepository;

    DirectoryScanLogDaoImpl(DirectoryScanLogRepository directoryScanLogRepository) {
        this.directoryScanLogRepository = directoryScanLogRepository;
    }

    @Override
    public void save(DirectoryScanLog directoryScanLog) {
        DirectoryScanLogEntity entity = new DirectoryScanLogEntity();
        entity.setStatus(directoryScanLog.getStatus());
        entity.setDirectory(directoryScanLog.getDirectory());
        entity.setCleanAllApplied(directoryScanLog.isCleanAllApplied());
        entity.setNumberOfSongsAdded(directoryScanLog.getNumberOfSongsAdded());

        Duration duration = directoryScanLog.getDuration();
        String durationString = "0";
        if (duration.getSeconds() > 0) {
            durationString = duration.getSeconds() + " second(s)";
        } else if (entity.getNumberOfSongsAdded() > 0) {
            durationString = TimeUnit.MILLISECONDS.convert(duration) + " milliseconds";
        }
        entity.setDuration(durationString);
        directoryScanLogRepository.save(entity);
    }

}
