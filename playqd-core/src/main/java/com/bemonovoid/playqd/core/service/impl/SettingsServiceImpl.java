package com.bemonovoid.playqd.core.service.impl;

import com.bemonovoid.playqd.core.dao.SettingsDao;
import com.bemonovoid.playqd.core.model.ScanOptions;
import com.bemonovoid.playqd.core.model.settings.LibrarySettings;
import com.bemonovoid.playqd.core.service.MusicLibraryScanner;
import com.bemonovoid.playqd.core.service.SettingsService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
class SettingsServiceImpl implements SettingsService {

    private final SettingsDao settingsDao;
    private final MusicLibraryScanner musicLibraryScanner;

    SettingsServiceImpl(SettingsDao settingsDao, MusicLibraryScanner musicLibraryScanner) {
        this.settingsDao = settingsDao;
        this.musicLibraryScanner = musicLibraryScanner;
    }

    @Override
    public LibrarySettings getLibrarySettings() {
        return settingsDao.getLibrarySettings();
    }

    @Override
    public void saveLibrarySettings(LibrarySettings librarySettings) {
        settingsDao.saveLibrarySettings(librarySettings);
    }

    @Override
    @Async
    public void rescanLibrary(LibrarySettings librarySettings) {
        ScanOptions options;
        if (librarySettings != null) {
            options = ScanOptions.builder()
                    .deleteMissing(librarySettings.isDeleteMissing())
                    .deleteAllBeforeScan(librarySettings.isDeleteBeforeScan())
                    .build();
        } else {
            options = ScanOptions.builder().build();
        }
        musicLibraryScanner.scan(options);
    }
}
