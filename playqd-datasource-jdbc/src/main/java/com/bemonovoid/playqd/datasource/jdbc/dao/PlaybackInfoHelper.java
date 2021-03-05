package com.bemonovoid.playqd.datasource.jdbc.dao;

import com.bemonovoid.playqd.core.model.PlaybackInfo;
import com.bemonovoid.playqd.datasource.jdbc.entity.PlaybackInfoEntity;

abstract class PlaybackInfoHelper {

    static PlaybackInfo fromEntity(PlaybackInfoEntity entity) {
        return new PlaybackInfo(
                entity.getSong().getId(),
                entity.getPlayCount(),
                entity.isFavorite(),
                entity.getLastModifiedDate());
    }
}
