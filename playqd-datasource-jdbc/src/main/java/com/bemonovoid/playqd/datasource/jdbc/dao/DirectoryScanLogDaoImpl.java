package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.bemonovoid.playqd.core.dao.DirectoryScanLogDao;
import com.bemonovoid.playqd.core.model.DirectoryScanLog;
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
    public void save(DirectoryScanLog directoryScanLog) {
        MusicDatabaseUpdateLogEntity entity = new MusicDatabaseUpdateLogEntity();
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
        musicDatabaseUpdateLogRepository.save(entity);
    }

}
