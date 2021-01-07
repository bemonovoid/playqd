package com.bemonovoid.playqd.library.service.impl;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.bemonovoid.playqd.library.service.MusicDirectory;

public class MusicDirectoryImpl implements MusicDirectory {

    private final String baseLocation;

    public MusicDirectoryImpl(String baseLocation) {
        this.baseLocation = baseLocation;
    }

    @Override
    public Path basePath() {
        return Paths.get(baseLocation);
    }
}
