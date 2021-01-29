package com.bemonovoid.playqd.datasource.jdbc.dao;

import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.datasource.jdbc.entity.ArtistEntity;

abstract class ArtistHelper {

    static Artist fromEntity(ArtistEntity artistEntity) {
        return Artist.builder()
                .id(artistEntity.getId())
                .name(artistEntity.getName())
                .simpleName(artistEntity.getSimpleName())
                .country(artistEntity.getCountry())
                .build();
    }

    static ArtistEntity toEntity(Artist artist) {

        ArtistEntity artistEntity = new ArtistEntity();

        artistEntity.setId(artist.getId());
        artistEntity.setName(artist.getName());
        artistEntity.setSimpleName(artist.getSimpleName());
        artistEntity.setCountry(artist.getCountry());

        return artistEntity;
    }
}
