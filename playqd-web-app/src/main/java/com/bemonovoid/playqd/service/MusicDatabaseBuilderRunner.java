package com.bemonovoid.playqd.service;

import com.bemonovoid.playqd.config.properties.AppProperties;
import com.bemonovoid.playqd.core.service.MusicDatabaseBuilder;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
class MusicDatabaseBuilderRunner implements ApplicationRunner {

    private final boolean scanOnStartup;
    private final MusicDatabaseBuilder musicDatabaseBuilder;

    MusicDatabaseBuilderRunner(AppProperties appProperties, MusicDatabaseBuilder musicDatabaseBuilder) {
        this.musicDatabaseBuilder = musicDatabaseBuilder;
        this.scanOnStartup = appProperties.getLibrary().isScanOnStartup();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (scanOnStartup) {
            musicDatabaseBuilder.build(false);
        }
    }
}
