package com.bemonovoid.playqd.datasource.jdbc.repository;

import java.util.List;

import com.bemonovoid.playqd.datasource.jdbc.projection.PlaybackHistoryArtistProjection;
import com.bemonovoid.playqd.datasource.jdbc.projection.PlaybackHistorySongProjection;
import com.bemonovoid.playqd.datasource.jdbc.entity.PlaybackHistoryEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface PlaybackHistoryRepository extends CrudRepository<PlaybackHistoryEntity, Long> {

    @Query("SELECT h.song.id as songId, s.album.id as albumId, s.artist.id as artistId, COUNT(h.song.id) as playCount, MAX(h.createdDate) as mostRecentPlayDateTime " +
            "FROM PlaybackHistoryEntity h " +
            "left join SongEntity s on h.song.id = s.id " +
            "GROUP BY songId, albumId, artistId " +
            "ORDER BY mostRecentPlayDateTime desc")
    List<PlaybackHistorySongProjection> groupBySongPlaybackHistory();

    @Query("SELECT s.artist.id as artistId, COUNT(h.song.id) as playCount, MAX(h.createdDate) as mostRecentPlayDateTime " +
            "FROM PlaybackHistoryEntity h " +
            "left join SongEntity s on h.song.id = s.id " +
            "GROUP BY artistId " +
            "ORDER BY mostRecentPlayDateTime desc")
    List<PlaybackHistoryArtistProjection> groupByArtistPlaybackHistory();
}
