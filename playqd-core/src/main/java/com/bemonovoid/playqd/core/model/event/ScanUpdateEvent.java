package com.bemonovoid.playqd.core.model.event;

import com.bemonovoid.playqd.core.model.ScannerLog;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ScanUpdateEvent extends ApplicationEvent {

    private final ScannerLog scannerLog;

    public ScanUpdateEvent(Object source, ScannerLog scannerLog) {
        super(source);
        this.scannerLog = scannerLog;
    }
}
