package com.bemonovoid.playqd.datasource.jdbc.dao;

import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.Image;
import com.bemonovoid.playqd.datasource.jdbc.entity.AlbumEntity;

abstract class AlbumHelper {

    static Album fromEntity(AlbumEntity entity) {
        return Album.builder()
                .id(entity.getUUID())
                .name(entity.getName())
                .simpleName(entity.getSimpleName())
                .genre(entity.getGenre())
                .date(entity.getDate())
                .totalTimeInSeconds(entity.getTotalTimeInSeconds())
                .artist(ArtistHelper.fromEntity(entity.getArtist()))
                .image(entity.getImage() == null ? null : new Image("", entity.getImage(), null))
                .build();
    }

    static AlbumEntity toEntity(Album album) {

        AlbumEntity albumEntity = new AlbumEntity();

        albumEntity.setUUID(album.getId());
        albumEntity.setName(album.getName());
        albumEntity.setSimpleName(album.getSimpleName());
        albumEntity.setDate(album.getDate());
        albumEntity.setGenre(album.getGenre());
        albumEntity.setImage(album.getImage().getData());
        albumEntity.setArtist(ArtistHelper.toEntity(album.getArtist()));

        return albumEntity;
    }

}
