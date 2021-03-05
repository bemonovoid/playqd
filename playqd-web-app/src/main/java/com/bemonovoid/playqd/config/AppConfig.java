package com.bemonovoid.playqd.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.bemonovoid.playqd.config.properties.AppProperties;
import com.bemonovoid.playqd.core.exception.PlayqdConfigurationException;
import com.bemonovoid.playqd.core.model.WorkingDir;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

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
        if (appProperties.getWorkDir() == null) {
            throw new PlayqdConfigurationException(
                    "Working directory is not set. 'playqd.workDir' property must be set");
        }
        Path workDirPath = Paths.get(appProperties.getWorkDir());
        if (!Files.exists(workDirPath)) {
            throw new PlayqdConfigurationException(
                    String.format("'%s' working directory path does not exist.", appProperties.getWorkDir()));
        }
        return new WorkingDir(workDirPath);
    }
}
