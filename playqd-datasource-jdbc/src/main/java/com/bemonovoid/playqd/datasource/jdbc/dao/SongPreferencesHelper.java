package com.bemonovoid.playqd.datasource.jdbc.dao;

import com.bemonovoid.playqd.core.model.SongPreferences;
import com.bemonovoid.playqd.datasource.jdbc.entity.SongPreferencesEntity;

abstract class SongPreferencesHelper {

    static SongPreferences fromEntity(SongPreferencesEntity entity) {
        return SongPreferences.builder()
                .songId(entity.getSong().getId())
                .songNameAsFileName(entity.isSongNameAsFileName())
                .build();

    }
}
