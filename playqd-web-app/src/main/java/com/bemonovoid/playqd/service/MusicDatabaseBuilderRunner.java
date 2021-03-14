package com.bemonovoid.playqd.service;

import com.bemonovoid.playqd.core.service.MusicDatabaseBuilder;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
class MusicDatabaseBuilderRunner implements ApplicationRunner {

    private final MusicDatabaseBuilder musicDatabaseBuilder;

    MusicDatabaseBuilderRunner(MusicDatabaseBuilder musicDatabaseBuilder) {
        this.musicDatabaseBuilder = musicDatabaseBuilder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        musicDatabaseBuilder.build(false);
    }
}
