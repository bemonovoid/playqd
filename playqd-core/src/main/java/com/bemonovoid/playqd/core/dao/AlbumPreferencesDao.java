package com.bemonovoid.playqd.core.dao;

import java.util.UUID;

import com.bemonovoid.playqd.core.model.AlbumPreferences;

public interface AlbumPreferencesDao {

    void save(String albumId, AlbumPreferences albumPreferences);
}
