package com.bemonovoid.playqd.datasource.jdbc.dao;

import com.bemonovoid.playqd.core.helpers.ResourceIdHelper;
import com.bemonovoid.playqd.core.model.LibraryResourceId;
import com.bemonovoid.playqd.core.model.PlaybackInfo;
import com.bemonovoid.playqd.core.model.ResourceTarget;
import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.service.SecurityService;
import com.bemonovoid.playqd.datasource.jdbc.entity.SongEntity;

abstract class SongHelper {

    static Song fromEntity(SongEntity songEntity) {
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

        song.setArtist(ArtistHelper.fromEntity(songEntity.getArtist()));
        song.setAlbum(AlbumHelper.fromEntity(songEntity.getAlbum()));

        String username = SecurityService.getCurrentUserName();

        song.setPlaybackInfo(PlaybackInfo.builder().build());

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
