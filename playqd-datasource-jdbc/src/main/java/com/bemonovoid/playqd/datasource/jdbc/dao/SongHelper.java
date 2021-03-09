package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.bemonovoid.playqd.core.helpers.ResourceIdHelper;
import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.LibraryResourceId;
import com.bemonovoid.playqd.core.model.PlaybackInfo;
import com.bemonovoid.playqd.core.model.ResourceTarget;
import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.service.SecurityService;
import com.bemonovoid.playqd.datasource.jdbc.entity.SongEntity;

abstract class SongHelper {

    static List<Song> fromAlbumSongEntities(List<SongEntity> entities) {
        if (entities.isEmpty()) {
            return Collections.emptyList();
        }
        Album songAlbum = AlbumHelper.fromEntity(entities.get(0).getAlbum());
        return entities.stream().map(songEntity -> fromEntity(songEntity, songAlbum)).collect(Collectors.toList());
    }

    static Song fromEntity(SongEntity songEntity) {
        return fromEntity(songEntity, null);
    }

    private static Song fromEntity(SongEntity songEntity, Album album) {
        Song song = new Song();

        song.setId(songEntity.getId());
        song.setName(songEntity.getName());
        song.setOriginalName(songEntity.getName());
        song.setComment(songEntity.getComment());
        song.setDuration(songEntity.getDuration());
        song.setOriginalTrackId(songEntity.getTrackId());
        song.setLyrics(songEntity.getLyrics());
        song.setTrackId(resolveTrackId(songEntity.getTrackId()));

        song.setAudioBitRate(songEntity.getAudioBitRate());
        song.setAudioChannelType(songEntity.getAudioChannelType());
        song.setAudioSampleRate(songEntity.getAudioSampleRate());
        song.setAudioEncodingType(songEntity.getAudioEncodingType());

        song.setFileLocation(songEntity.getFileLocation());
        song.setFileName(songEntity.getFileName());
        song.setFileExtension(songEntity.getFileExtension());

        song.setPlaybackInfo(PlaybackInfo.builder().build());

        Album songAlbum = album;
        if (songAlbum == null) {
            songAlbum = AlbumHelper.fromEntity(songEntity.getAlbum());
        }

        song.setArtist(songAlbum.getArtist());
        song.setAlbum(songAlbum);

        String username = SecurityService.getCurrentUserName();

        if (songEntity.getPlaybackInfo() != null && songEntity.getPlaybackInfo().size() > 0) {
            songEntity.getPlaybackInfo().stream()
                    .filter(playback -> playback.getCreatedBy().equals(username))
                    .findFirst()
                    .map(PlaybackInfoHelper::fromEntity)
                    .ifPresent(song::setPlaybackInfo);
        }

        if (songEntity.getPreferences() != null && songEntity.getPreferences().size() > 0) {
            songEntity.getPreferences().stream()
                    .filter(preferences -> preferences.getCreatedBy().equals(username))
                    .findFirst()
                    .map(SongPreferencesHelper::fromEntity)
                    .ifPresent(song::setPreferences);
        }

        var resourceId = new LibraryResourceId(song.getId(), ResourceTarget.SONG, SecurityService.getCurrentUserToken());
        song.setResourceId(ResourceIdHelper.encode(resourceId));

        return song;
    }

    private static int resolveTrackId(String trackId) {
        if (trackId != null && !trackId.isBlank()) {
            if (trackId.startsWith("0")) {
                return Integer.parseInt(trackId.replaceFirst("0", ""));
            }
            return Integer.parseInt(trackId);
        } else {
            return -1;
        }
    }
}
