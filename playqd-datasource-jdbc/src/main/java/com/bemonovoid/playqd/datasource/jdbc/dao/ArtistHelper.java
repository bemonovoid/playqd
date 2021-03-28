package com.bemonovoid.playqd.datasource.jdbc.dao;

import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.datasource.jdbc.entity.ArtistEntity;
import com.bemonovoid.playqd.datasource.jdbc.projection.ArtistIdAndNameProjection;
import com.bemonovoid.playqd.datasource.jdbc.projection.CountProjection;

abstract class ArtistHelper {

    static Artist fromEntity(ArtistEntity artistEntity) {
        return fromEntity(artistEntity, null);
    }

    static Artist fromEntity(ArtistEntity artistEntity, CountProjection countProjection) {
        Artist.ArtistBuilder artistBuilder = Artist.builder()
                .id(artistEntity.getUUID())
                .name(artistEntity.getName())
                .country(artistEntity.getCountry())
                .spotifyId(artistEntity.getSpotifyArtistId())
                .spotifyName(artistEntity.getSpotifyArtistName());
        if (countProjection != null) {
            artistBuilder
                    .albumCount(countProjection.getAlbumCount())
                    .songCount(countProjection.getSongCount());
        }
        return artistBuilder.build();
    }

    static Artist fromProjection(ArtistIdAndNameProjection projection) {
        return Artist.builder().id(projection.getId()).name(projection.getName()).build();
    }

    static ArtistEntity toEntity(Artist artist) {
        ArtistEntity artistEntity = new ArtistEntity();
        artistEntity.setUUID(artist.getId());
        artistEntity.setName(artist.getName());
        artistEntity.setCountry(artist.getCountry());
        artistEntity.setSpotifyArtistId(artist.getSpotifyId());
        artistEntity.setSpotifyArtistName(artist.getSpotifyName());
        return artistEntity;
    }
}
