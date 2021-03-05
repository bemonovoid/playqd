package com.bemonovoid.playqd.datasource.jdbc.repository;

import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.datasource.jdbc.entity.PlaybackInfoEntity;
import com.bemonovoid.playqd.datasource.jdbc.projection.PlaybackHistoryArtistProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PlaybackInfoRepository extends JpaRepository<PlaybackInfoEntity, Long> {

    Optional<PlaybackInfoEntity> findBySongIdAndCreatedBy(long songId, String createdBy);

    @Query("SELECT p.song.artist.id AS artistId, COUNT(p.song.id) AS playCount, MAX(p.createdDate) AS mostRecentPlayDateTime " +
            "FROM PlaybackInfoEntity p " +
            "WHERE p.createdBy = ?1 " +
            "GROUP BY artistId " +
            "ORDER BY mostRecentPlayDateTime DESC")
    List<PlaybackHistoryArtistProjection> groupByArtistPlaybackHistory(String createdBy);

}
