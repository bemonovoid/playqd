package com.bemonovoid.playqd.core.dao;

import com.bemonovoid.playqd.core.model.settings.LibrarySettings;

public interface SettingsDao {

    LibrarySettings getLibrarySettings();

    void saveLibrarySettings(LibrarySettings librarySettings);
}
