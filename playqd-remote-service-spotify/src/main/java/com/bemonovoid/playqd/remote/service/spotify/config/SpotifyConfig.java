package com.bemonovoid.playqd.remote.service.spotify.config;

import com.bemonovoid.playqd.core.dao.ArtistDao;
import com.bemonovoid.playqd.core.service.BinaryResourceReader;
import com.bemonovoid.playqd.core.service.ImageSearchService;
import com.bemonovoid.playqd.remote.service.spotify.impl.SpotifyApi;
import com.bemonovoid.playqd.remote.service.spotify.impl.SpotifyImageSearchService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableConfigurationProperties
class SpotifyConfig {

    @Bean
    @ConfigurationProperties(prefix = "playqd.remote.spotify")
    SpotifyProperties spotifyProperties() {
        return new SpotifyProperties();
    }

    @Bean
    @Primary
    @ConditionalOnProperty(prefix = "playqd.remote.spotify", name = "enabled", havingValue = "true")
    ImageSearchService imageSearchService(SpotifyProperties spotifyProperties,
                                          ArtistDao artistDao,
                                          BinaryResourceReader binaryResourceReader) {
        return new SpotifyImageSearchService(new SpotifyApi(spotifyProperties), artistDao, binaryResourceReader);
    }

}
