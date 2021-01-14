package com.bemonovoid.playqd.config;

import com.bemonovoid.playqd.data.dao.AlbumDao;
import com.bemonovoid.playqd.data.dao.ArtistDao;
import com.bemonovoid.playqd.data.dao.SongDao;
import com.bemonovoid.playqd.library.service.ArtworkService;
import com.bemonovoid.playqd.library.service.LibraryDirectory;
import com.bemonovoid.playqd.library.service.LibraryDirectoryScanner;
import com.bemonovoid.playqd.library.service.LibraryQueryService;
import com.bemonovoid.playqd.library.service.MusicBrainzApiClient;
import com.bemonovoid.playqd.library.service.MusicBrainzCoverArtApiClient;
import com.bemonovoid.playqd.library.service.MusicBrainzService;
import com.bemonovoid.playqd.library.service.impl.ArtworkServiceImpl;
import com.bemonovoid.playqd.library.service.impl.LibraryDirectoryImpl;
import com.bemonovoid.playqd.library.service.impl.LibraryDirectoryScannerImpl;
import com.bemonovoid.playqd.library.service.impl.LibraryQueryServiceImpl;
import com.bemonovoid.playqd.library.service.impl.MusicBrainzApiClientImpl;
import com.bemonovoid.playqd.library.service.impl.MusicBrainzCoverArtApiClientImpl;
import com.bemonovoid.playqd.library.service.impl.MusicBrainzServiceImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableConfigurationProperties
@EnableAsync
class AppConfig {

    @Bean
    @ConfigurationProperties(prefix = "playqd")
    AppProperties appProperties() {
        return new AppProperties();
    }

    @Bean
    LibraryDirectory musicDirectory(AppProperties appProperties) {
        return new LibraryDirectoryImpl(appProperties.getLibrary().getLocation());
    }

    @Bean
    LibraryQueryService libraryQueryService(ArtistDao artistDao, AlbumDao albumDao, SongDao songDao) {
        return new LibraryQueryServiceImpl(artistDao, albumDao, songDao);
    }

    @Bean
    MusicBrainzApiClient musicBrainzApiClient() {
        return new MusicBrainzApiClientImpl(
                new RestTemplateBuilder().rootUri("http://musicbrainz.org/ws/2").build());
    }

    @Bean
    MusicBrainzCoverArtApiClient coverArtApiClient() {
        return new MusicBrainzCoverArtApiClientImpl(
                new RestTemplateBuilder().rootUri("http://coverartarchive.org").build());
    }

    @Bean
    MusicBrainzService musicBrainzService(MusicBrainzApiClient musicBrainzApiClient,
                                          MusicBrainzCoverArtApiClient coverArtApiClient) {
        return new MusicBrainzServiceImpl(musicBrainzApiClient, coverArtApiClient);
    }

    @Bean
    LibraryDirectoryScanner refreshLibraryExecutor(JdbcTemplate jdbcTemplate, LibraryDirectory libraryDirectory) {
        return new LibraryDirectoryScannerImpl(jdbcTemplate, libraryDirectory);
    }

    @Bean
    ArtworkService artworkService(JdbcTemplate jdbcTemplate,
                                  SongDao songDao,
                                  MusicBrainzService musicBrainzService) {
        return new ArtworkServiceImpl(jdbcTemplate, songDao, musicBrainzService);
    }
}
