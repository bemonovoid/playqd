package com.bemonovoid.playqd.datasource.jdbc.repository;

import java.util.List;

import com.bemonovoid.playqd.datasource.jdbc.entity.PlaybackHistoryEntity;
import com.bemonovoid.playqd.datasource.jdbc.projection.PlaybackHistoryArtistProjection;
import com.bemonovoid.playqd.datasource.jdbc.projection.PlaybackHistorySongProjection;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface PlaybackHistoryRepository extends CrudRepository<PlaybackHistoryEntity, Long> {

    @Query("SELECT h.song.id AS songId, COUNT(h.song.id) AS playCount, MAX(h.createdDate) as mostRecentPlayDateTime " +
            "FROM PlaybackHistoryEntity h " +
            "GROUP BY songId " +
            "ORDER BY playCount DESC")
    List<PlaybackHistorySongProjection> findTopPlayedSongs(PageRequest page);

    @Query("SELECT h.song.id AS songId, COUNT(h.song.id) AS playCount, MAX(h.createdDate) as mostRecentPlayDateTime " +
            "FROM PlaybackHistoryEntity h " +
            "LEFT JOIN SongEntity s ON h.song.id = s.id " +
            "GROUP BY songId " +
            "ORDER BY mostRecentPlayDateTime DESC")
    List<PlaybackHistorySongProjection> findTopRecentlyPlayedSongs(PageRequest page);

    @Query("SELECT s.artist.id AS artistId, COUNT(h.song.id) AS playCount, MAX(h.createdDate) AS mostRecentPlayDateTime " +
            "FROM PlaybackHistoryEntity h " +
            "LEFT JOIN SongEntity s ON h.song.id = s.id " +
            "GROUP BY artistId " +
            "ORDER BY mostRecentPlayDateTime DESC")
    List<PlaybackHistoryArtistProjection> groupByArtistPlaybackHistory();

}
