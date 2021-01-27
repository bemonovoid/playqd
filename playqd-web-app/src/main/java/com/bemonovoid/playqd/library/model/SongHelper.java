package com.bemonovoid.playqd.library.model;

import com.bemonovoid.playqd.data.entity.SongEntity;

public abstract class SongHelper {

    public static Song fromEntity(SongEntity songEntity) {
        Song song = new Song();

        song.setId(songEntity.getId());
        song.setName(songEntity.getName());
        song.setComment(songEntity.getComment());
        song.setDuration(songEntity.getDuration());
        song.setAudioBitRate(songEntity.getAudioBitRate());
        song.setAudioChannelType(songEntity.getAudioChannelType());
        song.setAudioSampleRate(songEntity.getAudioSampleRate());
        song.setAudioEncodingType(songEntity.getAudioEncodingType());
        song.setTrackId(resolveTrackId(songEntity.getTrackId()));
        song.setFileLocation(songEntity.getFileLocation());
        song.setFileName(songEntity.getFileName());
        song.setFileExtension(songEntity.getFileExtension());

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
