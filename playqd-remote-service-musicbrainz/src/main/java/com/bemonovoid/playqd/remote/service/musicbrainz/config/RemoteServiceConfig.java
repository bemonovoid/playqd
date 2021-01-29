package com.bemonovoid.playqd.remote.service.musicbrainz.config;

import com.bemonovoid.playqd.core.service.ArtworkSearchService;
import com.bemonovoid.playqd.remote.service.musicbrainz.search.MusicBrainzApiClient;
import com.bemonovoid.playqd.remote.service.musicbrainz.search.MusicBrainzArtworkSearchService;
import com.bemonovoid.playqd.remote.service.musicbrainz.search.MusicBrainzCoverArtApiClient;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RemoteServiceConfig {

    @Bean
    ArtworkSearchService musicBrainzArtworkSearchService() {
        MusicBrainzApiClient musicBrainzApiClient = new MusicBrainzApiClient(
                new RestTemplateBuilder().rootUri("http://musicbrainz.org/ws/2").build());

        MusicBrainzCoverArtApiClient coverArtApiClient = new MusicBrainzCoverArtApiClient(
                new RestTemplateBuilder().rootUri("http://coverartarchive.org").build());

        return new MusicBrainzArtworkSearchService(musicBrainzApiClient, coverArtApiClient);
    }
}
