package com.bemonovoid.playqd.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.bemonovoid.playqd.config.properties.AppProperties;
import com.bemonovoid.playqd.core.exception.PlayqdConfigurationException;
import com.bemonovoid.playqd.core.model.WorkingDir;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@Configuration
@EnableAsync
@EnableConfigurationProperties
class AppConfig {

    @Bean
    @ConfigurationProperties(prefix = "playqd")
    AppProperties appProperties() {
        return new AppProperties();
    }

    @Bean
    WorkingDir workingDir(AppProperties appProperties) {
        Path workDirPath;
        if (appProperties.getWorkDir() == null) {
            String userHomeDir = System.getProperty("user.home");
            try {
                workDirPath = Paths.get(userHomeDir, ".playqd");
                if (!Files.exists(workDirPath)) {
                    Files.createDirectories(workDirPath);
                    log.info("'{}' property was not set. Setting to user's home '{}'",
                            AppProperties.WORK_DIR_PROP, workDirPath.toString());
                }
            } catch (IOException e) {
                throw new PlayqdConfigurationException(String.format(
                        "Failed to create default working dir in '%s'. Consider setting '%s' property",
                        userHomeDir, AppProperties.WORK_DIR_PROP));
            }
        } else {
            workDirPath = Paths.get(appProperties.getWorkDir());
            if (!Files.exists(workDirPath)) {
                throw new PlayqdConfigurationException(
                        String.format("'%s' (set in '%s') working directory path does not exist.",
                                appProperties.getWorkDir(), AppProperties.WORK_DIR_PROP));
            }
        }
        return new WorkingDir(workDirPath);
    }

}
