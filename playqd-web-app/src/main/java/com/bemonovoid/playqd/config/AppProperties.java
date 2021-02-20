package com.bemonovoid.playqd.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
public class AppProperties {

    private String workDir;
    private MusicDirectoryProperties library;

    @Getter
    @Setter(AccessLevel.PACKAGE)
    public static class MusicDirectoryProperties {
        private String location;
    }
}
