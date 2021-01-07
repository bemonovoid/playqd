package com.bemonovoid.playqd.config;

import com.bemonovoid.playqd.data.dao.AlbumDao;
import com.bemonovoid.playqd.data.dao.ArtistDao;
import com.bemonovoid.playqd.data.dao.SongDao;
import com.bemonovoid.playqd.library.service.LibraryQueryService;
import com.bemonovoid.playqd.library.service.MusicDirectory;
import com.bemonovoid.playqd.library.service.MusicDirectoryScanner;
import com.bemonovoid.playqd.library.service.impl.LibraryQueryServiceImpl;
import com.bemonovoid.playqd.library.service.impl.MusicDirectoryImpl;
import com.bemonovoid.playqd.library.service.impl.MusicDirectoryScannerImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    MusicDirectory musicDirectory(AppProperties appProperties) {
        return new MusicDirectoryImpl(appProperties.getLibrary().getLocation());
    }

    @Bean
    MusicDirectoryScanner refreshLibraryExecutor(ArtistDao artistDao, MusicDirectory musicDirectory) {
        return new MusicDirectoryScannerImpl(artistDao, musicDirectory);
    }

    @Bean
    LibraryQueryService libraryQueryService(ArtistDao artistDao,
                                            AlbumDao albumDao,
                                            SongDao songDao) {
        return new LibraryQueryServiceImpl(artistDao, albumDao, songDao);
    }
}
