package com.bemonovoid.playqd.config;

import com.bemonovoid.playqd.data.dao.AlbumDao;
import com.bemonovoid.playqd.data.dao.ArtistDao;
import com.bemonovoid.playqd.data.dao.SongDao;
import com.bemonovoid.playqd.data.dao.impl.AlbumDaoImpl;
import com.bemonovoid.playqd.data.dao.impl.ArtistDaoImpl;
import com.bemonovoid.playqd.data.dao.impl.SongDaoImpl;
import com.bemonovoid.playqd.data.repository.JpaAlbumRepository;
import com.bemonovoid.playqd.data.repository.JpaArtistRepository;
import com.bemonovoid.playqd.data.repository.JpaSongRepository;
import com.bemonovoid.playqd.utils.PackageHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = PackageHelper.REPOSITORY_BASE_PACKAGE)
@EnableTransactionManagement
public class JpaConfig {

    @Bean
    ArtistDao artistDao(JpaArtistRepository repository) {
        return new ArtistDaoImpl(repository);
    }

    @Bean
    AlbumDao albumDao(JpaAlbumRepository repository) {
        return new AlbumDaoImpl(repository);
    }

    @Bean
    SongDao songDao(JpaSongRepository repository) {
        return new SongDaoImpl(repository);
    }
}
