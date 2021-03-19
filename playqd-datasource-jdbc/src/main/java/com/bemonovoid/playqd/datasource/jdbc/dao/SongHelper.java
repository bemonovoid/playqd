package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.service.SecurityService;
import com.bemonovoid.playqd.datasource.jdbc.entity.SongEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.SongPreferencesEntity;
import org.springframework.stereotype.Component;

@Component
class SongHelper {

    List<Song> fromAlbumSongEntities(List<SongEntity> entities,
                                            Map<String, SongPreferencesEntity> songPreferences) {
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

    Song fromEntity(SongEntity songEntity) {
        return fromEntity(songEntity, true);
    }

    private Song fromEntity(SongEntity songEntity, boolean deepCopy) {
        Song song = new Song();

        song.setId(songEntity.getUUID());
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

        if (deepCopy) {
            Album songAlbum = AlbumHelper.fromEntity(songEntity.getAlbum());
            song.setArtist(songAlbum.getArtist());
            song.setAlbum(songAlbum);

            if (songEntity.getPreferences() != null && songEntity.getPreferences().size() > 0) {
                songEntity.getPreferences().stream()
                        .filter(preferences -> preferences.getCreatedBy().equals(SecurityService.getCurrentUserName()))
                        .findFirst()
                        .map(SongPreferencesHelper::fromEntity)
                        .ifPresent(song::setPreferences);
            }
        }

        return song;
    }
}
