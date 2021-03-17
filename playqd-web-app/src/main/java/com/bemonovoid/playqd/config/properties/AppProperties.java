package com.bemonovoid.playqd.config.properties;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter(AccessLevel.PACKAGE)
@Validated
public class AppProperties {

    public static final String WORK_DIR_PROP = "playqd.workDir";

    private String workDir;

    @Valid
    @NotNull
    private SecurityProperties security;

    @Valid
    private MusicDirectoryProperties library;

    @Getter
    @Setter(AccessLevel.PACKAGE)
    public static class MusicDirectoryProperties {

        @NotEmpty
        private String musicDir;
        private boolean scanOnStartup;
    }

    @Getter
    @Setter(AccessLevel.PACKAGE)
    public static class SecurityProperties {

        private boolean enabled;

        @Valid
        @NotNull
        private ApplicationMainUser user;

        @NotEmpty
        private String tokenSecret;
    }

    @Getter
    @Setter(AccessLevel.PACKAGE)
    public static class ApplicationMainUser {

        @NotEmpty
        private String login;

        @NotEmpty
        private String password;
    }
}
