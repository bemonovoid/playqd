package com.bemonovoid.playqd.datasource.jdbc.dao;

import com.bemonovoid.playqd.core.helpers.ResourceIdHelper;
import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.LibraryResourceId;
import com.bemonovoid.playqd.core.model.ResourceTarget;
import com.bemonovoid.playqd.core.service.SecurityService;
import com.bemonovoid.playqd.datasource.jdbc.entity.ArtistEntity;
import com.bemonovoid.playqd.datasource.jdbc.projection.CountProjection;

abstract class ArtistHelper {

    static Artist fromEntity(ArtistEntity artistEntity) {
        return fromEntity(artistEntity, null);
    }

    static Artist fromEntity(ArtistEntity artistEntity, CountProjection countProjection) {
        LibraryResourceId resourceId = new LibraryResourceId(
                artistEntity.getId(), ResourceTarget.ARTIST, SecurityService.getCurrentUserToken());

        Artist.ArtistBuilder artistBuilder = Artist.builder()
                .id(artistEntity.getId())
                .spotifyId(artistEntity.getSpotifyArtistId())
                .name(artistEntity.getName())
                .simpleName(artistEntity.getSimpleName())
                .country(artistEntity.getCountry())
                .resourceId(ResourceIdHelper.encode(resourceId));
        if (countProjection != null) {
            artistBuilder
                    .albumCount(countProjection.getAlbumCount())
                    .songCount(countProjection.getSongCount());
        }
        return artistBuilder.build();
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
