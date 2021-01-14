package com.bemonovoid.playqd.config;

import com.bemonovoid.playqd.data.dao.AlbumDao;
import com.bemonovoid.playqd.data.dao.ArtistDao;
import com.bemonovoid.playqd.data.dao.SongDao;
import com.bemonovoid.playqd.library.service.ArtworkService;
import com.bemonovoid.playqd.library.service.LibraryDirectory;
import com.bemonovoid.playqd.library.service.LibraryDirectoryScanner;
import com.bemonovoid.playqd.library.service.LibraryQueryService;
import com.bemonovoid.playqd.library.service.impl.ArtworkServiceImpl;
import com.bemonovoid.playqd.library.service.impl.LibraryDirectoryImpl;
import com.bemonovoid.playqd.library.service.impl.LibraryDirectoryScannerImpl;
import com.bemonovoid.playqd.library.service.impl.LibraryQueryServiceImpl;
import com.bemonovoid.playqd.online.search.ArtworkOnlineSearchService;
import com.bemonovoid.playqd.online.search.mb.MusicBrainzApiClient;
import com.bemonovoid.playqd.online.search.mb.MusicBrainzArtworkOnlineSearch;
import com.bemonovoid.playqd.online.search.mb.MusicBrainzCoverArtApiClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationEventPublisher;
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
    LibraryDirectoryScanner refreshLibraryExecutor(JdbcTemplate jdbcTemplate, LibraryDirectory libraryDirectory) {
        return new LibraryDirectoryScannerImpl(jdbcTemplate, libraryDirectory);
    }

    @Bean
    ArtworkService artworkService(SongDao songDao, AlbumDao albumDao, ApplicationEventPublisher publisher) {
        MusicBrainzApiClient musicBrainzApiClient = new MusicBrainzApiClient(
                new RestTemplateBuilder().rootUri("http://musicbrainz.org/ws/2").build());

        MusicBrainzCoverArtApiClient coverArtApiClient = new MusicBrainzCoverArtApiClient(
                new RestTemplateBuilder().rootUri("http://coverartarchive.org").build());

        ArtworkOnlineSearchService artworkOnlineSearch =
                new MusicBrainzArtworkOnlineSearch(musicBrainzApiClient, coverArtApiClient);

        return new ArtworkServiceImpl(songDao, albumDao, artworkOnlineSearch, publisher);
    }
}
