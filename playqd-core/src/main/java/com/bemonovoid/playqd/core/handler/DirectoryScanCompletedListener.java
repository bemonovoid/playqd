package com.bemonovoid.playqd.core.handler;

import com.bemonovoid.playqd.core.model.event.ScanCompletedEvent;
import com.bemonovoid.playqd.core.service.DirectoryScanLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class DirectoryScanCompletedListener implements ApplicationListener<ScanCompletedEvent> {

    private final DirectoryScanLogService directoryScanLogService;

    DirectoryScanCompletedListener(DirectoryScanLogService directoryScanLogService) {
        this.directoryScanLogService = directoryScanLogService;
    }

    @Override
    public void onApplicationEvent(ScanCompletedEvent event) {
        directoryScanLogService.save(event.getScannerLog());
    }
}
