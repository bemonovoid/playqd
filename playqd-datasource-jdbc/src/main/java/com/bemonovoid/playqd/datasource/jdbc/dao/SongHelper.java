package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.bemonovoid.playqd.core.helpers.ResourceIdHelper;
import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.LibraryResourceId;
import com.bemonovoid.playqd.core.model.ResourceTarget;
import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.service.SecurityService;
import com.bemonovoid.playqd.datasource.jdbc.entity.SongEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.SongPreferencesEntity;

abstract class SongHelper {

    static List<Song> fromAlbumSongEntities(List<SongEntity> entities,
                                            Map<Long, SongPreferencesEntity> songPreferences) {
        if (entities.isEmpty()) {
            return Collections.emptyList();
        }
        Album songAlbum = AlbumHelper.fromEntity(entities.get(0).getAlbum());
        return entities.stream().map(songEntity -> {
            Song song = fromEntity(songEntity, false);
            song.setAlbum(songAlbum);
            song.setArtist(songAlbum.getArtist());
            if (songPreferences.containsKey(song.getId())) {
                song.setPreferences(SongPreferencesHelper.fromEntity(songPreferences.get(song.getId())));
            }
            return song;
        }).collect(Collectors.toList());
    }

    static Song fromEntity(SongEntity songEntity) {
        return fromEntity(songEntity, true);
    }

    private static Song fromEntity(SongEntity songEntity, boolean deepCopy) {
        Song song = new Song();

        song.setId(songEntity.getId());
        song.setName(songEntity.getName());
        song.setOriginalName(songEntity.getName());
        song.setComment(songEntity.getComment());
        song.setDuration(songEntity.getDuration());
        song.setLyrics(songEntity.getLyrics());
        song.setTrackId(songEntity.getTrackId() != null ? songEntity.getTrackId().toString() : null);

        song.setAudioBitRate(songEntity.getAudioBitRate());
        song.setAudioChannelType(songEntity.getAudioChannelType());
        song.setAudioSampleRate(songEntity.getAudioSampleRate());
        song.setAudioEncodingType(songEntity.getAudioEncodingType());

        song.setFileLocation(songEntity.getFileLocation());
        song.setFileName(songEntity.getFileName());
        song.setFileExtension(songEntity.getFileExtension());

        song.setFavorite(songEntity.isFavorite());
        song.setPlayCount(songEntity.getPlayCount());
        song.setLastPlayedTime(songEntity.getLastModifiedDate());

        String username = SecurityService.getCurrentUserName();

        if (deepCopy) {
            Album songAlbum = AlbumHelper.fromEntity(songEntity.getAlbum());
            song.setArtist(songAlbum.getArtist());
            song.setAlbum(songAlbum);

            if (songEntity.getPreferences() != null && songEntity.getPreferences().size() > 0) {
                songEntity.getPreferences().stream()
                        .filter(preferences -> preferences.getCreatedBy().equals(username))
                        .findFirst()
                        .map(SongPreferencesHelper::fromEntity)
                        .ifPresent(song::setPreferences);
            }
        }

        var resourceId = new LibraryResourceId(song.getId(), ResourceTarget.SONG, SecurityService.getCurrentUserToken());
        song.setResourceId(ResourceIdHelper.encode(resourceId));

        return song;
    }
}
