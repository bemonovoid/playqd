package com.bemonovoid.playqd.datasource.jdbc.dao;

import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.service.SecurityService;
import com.bemonovoid.playqd.datasource.jdbc.entity.SongEntity;
import org.springframework.stereotype.Component;

@Component
class SongHelper {

    Song fromEntity(SongEntity songEntity) {
        return fromEntity(songEntity, true);
    }

    Song fromEntity(SongEntity songEntity, boolean deepCopy) {
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
        }

        return song;
    }
}
