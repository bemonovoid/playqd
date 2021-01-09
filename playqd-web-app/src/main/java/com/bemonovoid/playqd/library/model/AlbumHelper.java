package com.bemonovoid.playqd.library.model;

import java.time.LocalTime;

import com.bemonovoid.playqd.data.entity.AlbumEntity;
import com.bemonovoid.playqd.data.entity.SongEntity;

public abstract class AlbumHelper {

    public static Album fromEntity(AlbumEntity entity) {
        int totalSeconds = entity.getSongs().stream()
                .map(SongEntity::getDuration)
                .mapToInt(Integer::intValue)
                .sum();
        return new Album(
                entity.getId(),
                entity.getName(),
                entity.getGenre(),
                entity.getDate(),
                secondsToMinutesAndSecondsString(totalSeconds),
                new Artist(entity.getArtist().getId(), entity.getArtist().getName()));
    }

    private static String secondsToMinutesAndSecondsString(long secs) {
        LocalTime time = LocalTime.of(0, 0, 0);
        LocalTime newTime = time.plusSeconds(secs);
        String result = "";
        if (newTime.getHour() == 1) {
            result = newTime.getHour() + " hour ";
        }
        if (newTime.getHour() > 1) {
            result = newTime.getHour() + " hours ";
        }
        if (newTime.getMinute() == 1) {
            result += newTime.getMinute() + " minute ";
        }
        if (newTime.getMinute() > 1) {
            result += newTime.getMinute() + " minutes ";
        }
        if (result.isEmpty()) {
            result = newTime.getSecond() + " seconds";
        }
        return result;
    }
}
