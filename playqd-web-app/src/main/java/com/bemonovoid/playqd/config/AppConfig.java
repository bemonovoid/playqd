package com.bemonovoid.playqd.config;

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
}
