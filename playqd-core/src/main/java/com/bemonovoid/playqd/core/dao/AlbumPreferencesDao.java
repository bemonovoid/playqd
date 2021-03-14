package com.bemonovoid.playqd.core.dao;

import com.bemonovoid.playqd.core.model.AlbumPreferences;

public interface AlbumPreferencesDao {

    void save(long albumId, AlbumPreferences albumPreferences);
}
