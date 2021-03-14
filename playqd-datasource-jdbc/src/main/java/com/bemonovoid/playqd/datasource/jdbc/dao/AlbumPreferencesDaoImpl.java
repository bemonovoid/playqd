package com.bemonovoid.playqd.datasource.jdbc.dao;

import com.bemonovoid.playqd.core.dao.AlbumPreferencesDao;
import com.bemonovoid.playqd.core.model.AlbumPreferences;
import com.bemonovoid.playqd.core.service.SecurityService;
import com.bemonovoid.playqd.datasource.jdbc.entity.AlbumEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.AlbumPreferencesEntity;
import com.bemonovoid.playqd.datasource.jdbc.repository.AlbumPreferencesRepository;
import com.bemonovoid.playqd.datasource.jdbc.repository.AlbumRepository;
import org.springframework.stereotype.Component;

@Component
class AlbumPreferencesDaoImpl implements AlbumPreferencesDao {

    private final AlbumRepository albumRepository;
    private final AlbumPreferencesRepository albumPreferencesRepository;

    AlbumPreferencesDaoImpl(AlbumRepository albumRepository, AlbumPreferencesRepository albumPreferencesRepository) {
        this.albumRepository = albumRepository;
        this.albumPreferencesRepository = albumPreferencesRepository;
    }

    @Override
    public void save(long albumId, AlbumPreferences albumPreferences) {
        String userName = SecurityService.getCurrentUserName();
        AlbumPreferencesEntity entity =
                albumPreferencesRepository.findByAlbumIdAndCreatedBy(albumId, userName)
                        .orElseGet(() -> {
                            AlbumEntity albumEntity = albumRepository.findOne(albumId);
                            AlbumPreferencesEntity e = new AlbumPreferencesEntity();
                            e.setAlbum(albumEntity);
                            return e;
            });
        entity.setSongNameAsFileName(albumPreferences.isSongNameAsFileName());
        albumPreferencesRepository.save(entity);
    }
}
