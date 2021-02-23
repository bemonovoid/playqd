package com.bemonovoid.playqd.core.model.event;

import com.bemonovoid.playqd.core.model.DirectoryScanLog;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DirectoryScanCompleted extends ApplicationEvent {

    private final DirectoryScanLog directoryScanLog;

    public DirectoryScanCompleted(Object source, DirectoryScanLog directoryScanLog) {
        super(source);
        this.directoryScanLog = directoryScanLog;
    }
}
