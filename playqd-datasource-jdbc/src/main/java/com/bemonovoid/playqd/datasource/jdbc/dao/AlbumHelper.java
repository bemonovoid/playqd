package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.time.LocalTime;

import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.Artwork;
import com.bemonovoid.playqd.datasource.jdbc.entity.AlbumEntity;

abstract class AlbumHelper {

    static Album fromEntity(AlbumEntity entity) {
        return Album.builder()
                .id(entity.getId())
                .name(entity.getName())
                .simpleName(entity.getSimpleName())
                .genre(entity.getGenre())
                .date(entity.getDate())
                .totalTimeInSeconds(entity.getTotalTimeInSeconds())
                .totalTimeHumanReadable(secondsToHumanReadableString(entity.getTotalTimeInSeconds()))
                .artist(ArtistHelper.fromEntity(entity.getArtist()))
                .artwork(Artwork.builder().binary(entity.getArtworkBinary()).build())
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
        albumEntity.setArtworkBinary(album.getArtwork().getBinary());
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
}
