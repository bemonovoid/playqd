package com.bemonovoid.playqd.service;

import com.bemonovoid.playqd.core.service.MusicLibraryScanner;
import com.bemonovoid.playqd.core.service.SettingsService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
class MusicDatabaseBuilderRunner implements ApplicationRunner {

    private final SettingsService settingsService;
    private final MusicLibraryScanner musicLibraryScanner;

    MusicDatabaseBuilderRunner(SettingsService settingsService, MusicLibraryScanner musicLibraryScanner) {
        this.settingsService = settingsService;
        this.musicLibraryScanner = musicLibraryScanner;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (settingsService.getLibrarySettings().isRescanAtStartup()) {
            musicLibraryScanner.scan(false);
        }
    }
}
