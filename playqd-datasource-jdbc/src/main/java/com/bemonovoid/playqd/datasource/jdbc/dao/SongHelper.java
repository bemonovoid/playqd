package com.bemonovoid.playqd.datasource.jdbc.dao;

import java.util.Comparator;

import com.bemonovoid.playqd.core.model.PlaybackHistorySong;
import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.datasource.jdbc.entity.PersistentAuditableEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.SongEntity;

abstract class SongHelper {

    static Song fromEntity(SongEntity songEntity) {
        return fromEntity(songEntity, null);
    }

    static Song fromEntity(SongEntity songEntity, PlaybackHistorySong playbackHistorySong) {
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

        song.setShowFileNameAsSongName(songEntity.getShowFileNameAsSongName());
        song.setFavorite(songEntity.isFavorite());

        if (song.isShowFileNameAsSongName()) {
            song.setName(songEntity.getFileName());
        }

        if (playbackHistorySong != null) {
            song.setPlaybackHistory(playbackHistorySong);
        } else if (songEntity.getPlayBackHistory().size() > 0) {
            songEntity.getPlayBackHistory().stream()
                    .max(Comparator.comparing(PersistentAuditableEntity::getCreatedDate))
                    .map(historyEntity -> new PlaybackHistorySong(
                            songEntity.getId(), songEntity.getPlayBackHistory().size(), historyEntity.getCreatedDate()))
                    .ifPresent(song::setPlaybackHistory);
        }
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
