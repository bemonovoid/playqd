package com.bemonovoid.playqd.core.service;

import com.bemonovoid.playqd.core.model.settings.LibrarySettings;

public interface SettingsService {

    LibrarySettings getLibrarySettings();

    void saveLibrarySettings(LibrarySettings librarySettings);

    void rescanLibrary(LibrarySettings librarySettings);

}
