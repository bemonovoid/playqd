package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.time.LocalDateTime;

import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.PlaybackHistoryArtist;
import com.bemonovoid.playqd.datasource.jdbc.entity.ArtistEntity;

abstract class ArtistHelper {

    static Artist fromEntity(ArtistEntity artistEntity) {
        return fromEntityInternal(artistEntity);
    }
    static Artist fromEntity(ArtistEntity artistEntity, PlaybackHistoryArtist playbackHistory) {
        return fromEntityInternal(artistEntity, playbackHistory);
    }

    static ArtistEntity toEntity(Artist artist) {

        ArtistEntity artistEntity = new ArtistEntity();

        artistEntity.setId(artist.getId());
        artistEntity.setName(artist.getName());
        artistEntity.setSimpleName(artist.getSimpleName());
        artistEntity.setCountry(artist.getCountry());

        return artistEntity;
    }

    private static Artist fromEntityInternal(ArtistEntity artistEntity) {
        return fromEntityInternal(artistEntity, null);
    }

    private static Artist fromEntityInternal(ArtistEntity artistEntity, PlaybackHistoryArtist playbackHistoryArtist) {
        return Artist.builder()
                .id(artistEntity.getId())
                .name(artistEntity.getName())
                .simpleName(artistEntity.getSimpleName())
                .country(artistEntity.getCountry())
                .playbackHistory(playbackHistoryArtist != null ? playbackHistoryArtist : new PlaybackHistoryArtist(artistEntity.getId(), 0, LocalDateTime.MIN.toString()))
                .build();
    }
}
