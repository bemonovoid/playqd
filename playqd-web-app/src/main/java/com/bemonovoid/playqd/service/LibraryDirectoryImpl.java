package com.bemonovoid.playqd.service;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.bemonovoid.playqd.config.AppProperties;
import com.bemonovoid.playqd.core.service.LibraryDirectory;
import org.springframework.stereotype.Component;

@Component
class LibraryDirectoryImpl implements LibraryDirectory {

    private final String baseLocation;

    LibraryDirectoryImpl(AppProperties appProperties) {
        this.baseLocation = appProperties.getLibrary().getLocation();
    }

    @Override
    public Path basePath() {
        return Paths.get(baseLocation);
    }
}
