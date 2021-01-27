package com.bemonovoid.playqd.library.model;

import com.bemonovoid.playqd.data.entity.ArtistEntity;

public abstract class ArtistHelper {

    public static Artist fromEntity(ArtistEntity artistEntity) {
        return Artist.builder()
                .id(artistEntity.getId())
                .name(artistEntity.getName())
                .country(artistEntity.getCountry())
                .build();
    }
}
