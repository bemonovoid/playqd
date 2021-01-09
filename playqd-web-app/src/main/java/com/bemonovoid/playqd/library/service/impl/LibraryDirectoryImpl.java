package com.bemonovoid.playqd.library.service.impl;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.bemonovoid.playqd.library.service.LibraryDirectory;

public class LibraryDirectoryImpl implements LibraryDirectory {

    private final String baseLocation;

    public LibraryDirectoryImpl(String baseLocation) {
        this.baseLocation = baseLocation;
    }

    @Override
    public Path basePath() {
        return Paths.get(baseLocation);
    }
}
