package com.bemonovoid.playqd.datasource.jdbc.dao;

import com.bemonovoid.playqd.core.model.PlaybackInfo;
import com.bemonovoid.playqd.datasource.jdbc.entity.PlaybackInfoEntity;

abstract class PlaybackInfoHelper {

    static PlaybackInfo fromEntity(PlaybackInfoEntity entity) {
        return PlaybackInfo.builder()
                .songId(entity.getSong().getId())
                .playCount(entity.getPlayCount())
                .favorite(entity.isFavorite())
                .lastPlayedTime(entity.getLastModifiedDate())
                .build();
    }
}
