package com.bemonovoid.playqd.config.properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
public class AppProperties {

    private String workDir;
    private SecurityProperties security;
    private MusicDirectoryProperties library;

    @Getter
    @Setter(AccessLevel.PACKAGE)
    public static class MusicDirectoryProperties {
        private String location;
    }

    @Getter
    @Setter(AccessLevel.PACKAGE)
    public static class SecurityProperties {
        private ApplicationMainUser user;
        private String tokenSecret;
    }

    @Getter
    @Setter(AccessLevel.PACKAGE)
    public static class ApplicationMainUser {
        private String login;
        private String password;
    }
}
