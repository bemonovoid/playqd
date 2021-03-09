package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.time.LocalTime;

import com.bemonovoid.playqd.core.helpers.ResourceIdHelper;
import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.AlbumPreferences;
import com.bemonovoid.playqd.core.model.Image;
import com.bemonovoid.playqd.core.model.LibraryResourceId;
import com.bemonovoid.playqd.core.model.ResourceTarget;
import com.bemonovoid.playqd.core.service.SecurityService;
import com.bemonovoid.playqd.datasource.jdbc.entity.AlbumEntity;

abstract class AlbumHelper {

    static Album fromEntity(AlbumEntity entity) {
        var resourceId =
                new LibraryResourceId(entity.getId(), ResourceTarget.ALBUM, SecurityService.getCurrentUserToken());
        return Album.builder()
                .id(entity.getId())
                .name(entity.getName())
                .simpleName(entity.getSimpleName())
                .genre(entity.getGenre())
                .date(entity.getDate())
                .totalTimeInSeconds(entity.getTotalTimeInSeconds())
                .totalTimeHumanReadable(secondsToHumanReadableString(entity.getTotalTimeInSeconds()))
                .artist(ArtistHelper.fromEntity(entity.getArtist()))
                .image(entity.getImage() == null ? null : new Image("", entity.getImage(), null))
                .preferences(albumPreferences(entity))
                .resourceId(ResourceIdHelper.encode(resourceId))
                .build();
    }

    static AlbumEntity toEntity(Album album) {

        AlbumEntity albumEntity = new AlbumEntity();

        albumEntity.setId(album.getId());
        albumEntity.setName(album.getName());
        albumEntity.setSimpleName(album.getSimpleName());
        albumEntity.setDate(album.getDate());
        albumEntity.setGenre(album.getGenre());
        albumEntity.setMbReleaseId(album.getMbReleaseId());
        albumEntity.setImage(album.getImage().getData());
        albumEntity.setArtist(ArtistHelper.toEntity(album.getArtist()));

        return albumEntity;
    }

    private static String secondsToHumanReadableString(long secs) {
        LocalTime time = LocalTime.of(0, 0, 0);
        LocalTime newTime = time.plusSeconds(secs);
        String result = "";
        if (newTime.getHour() == 1) {
            result = newTime.getHour() + " hour ";
        }
        if (newTime.getHour() > 1) {
            result = newTime.getHour() + " hours ";
        }
        if (newTime.getMinute() == 1) {
            result += newTime.getMinute() + " minute ";
        }
        if (newTime.getMinute() > 1) {
            result += newTime.getMinute() + " minutes ";
        }
        if (result.isEmpty()) {
            result = newTime.getSecond() + " seconds";
        }
        return result;
    }

    private static AlbumPreferences albumPreferences(AlbumEntity entity) {
        AlbumPreferences preferences = null;
        String username = SecurityService.getCurrentUserName();
        if (entity.getPreferences() != null && entity.getPreferences().size() > 0) {
            preferences = entity.getPreferences().stream()
                    .filter(preferencesEntity -> preferencesEntity.getCreatedBy().equals(username))
                    .findFirst()
                    .map(AlbumPreferencesHelper::fromEntity)
                    .orElse(null);
        }
        return preferences;
    }
}
