package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.util.List;

import com.bemonovoid.playqd.core.dao.SettingsDao;
import com.bemonovoid.playqd.core.model.settings.LibrarySettings;
import com.bemonovoid.playqd.datasource.jdbc.entity.LibrarySettingsEntity;
import com.bemonovoid.playqd.datasource.jdbc.repository.LibrarySettingsRepository;
import org.springframework.stereotype.Component;

@Component
class SettingsDaoImpl implements SettingsDao {

    private final LibrarySettingsRepository librarySettingsRepository;

    SettingsDaoImpl(LibrarySettingsRepository librarySettingsRepository) {
        this.librarySettingsRepository = librarySettingsRepository;
    }

    @Override
    public LibrarySettings getLibrarySettings() {
        List<LibrarySettingsEntity> settings = librarySettingsRepository.findAll();
        if (settings.isEmpty()) {
            return new LibrarySettings();
        }
        LibrarySettingsEntity entity = settings.get(0);
        LibrarySettings librarySettings = new LibrarySettings();
        librarySettings.setRescanAtStartup(entity.isRescanLibraryAtStartup());
        librarySettings.setDeleteBeforeScan(entity.isDeleteLibraryBeforeScan());
        return librarySettings;
    }

    @Override
    public void saveLibrarySettings(LibrarySettings librarySettings) {
        LibrarySettingsEntity entity = librarySettingsRepository.findAll().stream()
                .findFirst()
                .orElseGet(LibrarySettingsEntity::new);
        entity.setRescanLibraryAtStartup(librarySettings.isRescanAtStartup());
        entity.setDeleteLibraryBeforeScan(librarySettings.isDeleteBeforeScan());
        librarySettingsRepository.save(entity);
    }
}
