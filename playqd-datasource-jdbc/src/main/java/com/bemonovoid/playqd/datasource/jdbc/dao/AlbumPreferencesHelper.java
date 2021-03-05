package com.bemonovoid.playqd.datasource.jdbc.dao;

import com.bemonovoid.playqd.core.model.AlbumPreferences;
import com.bemonovoid.playqd.datasource.jdbc.entity.AlbumPreferencesEntity;

abstract class AlbumPreferencesHelper {

    static AlbumPreferences fromEntity(AlbumPreferencesEntity entity) {
        AlbumPreferences preferences = new AlbumPreferences();
        preferences.setAlbumId(entity.getAlbum().getId());
        preferences.setSongNameAsFileName(entity.isSongNameAsFileName());
        return preferences;
    }
}
