package com.bemonovoid.playqd.config;

import com.bemonovoid.playqd.data.dao.AlbumDao;
import com.bemonovoid.playqd.data.dao.ArtistDao;
import com.bemonovoid.playqd.data.dao.SongDao;
import com.bemonovoid.playqd.library.service.LibraryDirectory;
import com.bemonovoid.playqd.library.service.LibraryDirectoryScanner;
import com.bemonovoid.playqd.library.service.LibraryQueryService;
import com.bemonovoid.playqd.library.service.impl.LibraryDirectoryImpl;
import com.bemonovoid.playqd.library.service.impl.LibraryDirectoryScannerImpl;
import com.bemonovoid.playqd.library.service.impl.LibraryQueryServiceImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
    LibraryDirectoryScanner refreshLibraryExecutor(JdbcTemplate jdbcTemplate, LibraryDirectory libraryDirectory) {
        return new LibraryDirectoryScannerImpl(jdbcTemplate, libraryDirectory);
    }

    @Bean LibraryQueryService libraryQueryService(ArtistDao artistDao, AlbumDao albumDao, SongDao songDao) {
        return new LibraryQueryServiceImpl(artistDao, albumDao, songDao);
    }
}
