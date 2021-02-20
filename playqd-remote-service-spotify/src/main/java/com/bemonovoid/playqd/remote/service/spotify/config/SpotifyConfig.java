package com.bemonovoid.playqd.remote.service.spotify.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
class SpotifyConfig {

    @Bean
    @ConfigurationProperties(prefix = "playqd.remote.spotify")
    SpotifyProperties spotifyProperties() {
        return new SpotifyProperties();
    }
}
