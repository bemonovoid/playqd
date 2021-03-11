package com.bemonovoid.playqd.datasource.jdbc.repository;

import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.datasource.jdbc.entity.ArtistEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.PlaybackInfoEntity;
import com.bemonovoid.playqd.datasource.jdbc.entity.SongEntity;
import com.bemonovoid.playqd.datasource.jdbc.projection.PlaybackHistoryArtistProjection;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import com.bemonovoid.playqd.datasource.jdbc.projection.RecentlyPlayedSongProjection;

public interface PlaybackInfoRepository extends JpaRepository<PlaybackInfoEntity, Long> {

    Optional<PlaybackInfoEntity> findBySongIdAndCreatedBy(long songId, String createdBy);

    @Query("SELECT p.song.artist.id AS artistId, COUNT(p.song.id) AS playCount, MAX(p.createdDate) AS mostRecentPlayDateTime " +
            "FROM PlaybackInfoEntity p " +
            "WHERE p.createdBy = ?1 " +
            "GROUP BY artistId " +
            "ORDER BY mostRecentPlayDateTime DESC")
    List<PlaybackHistoryArtistProjection> groupByArtistPlaybackHistory(String createdBy);

    @Query("SELECT p.song.id " +
            "FROM PlaybackInfoEntity p " +
            "WHERE p.createdBy = ?1 AND p.playCount > 0 " +
            "GROUP BY p.song.id " +
            "ORDER BY MAX(p.createdDate) DESC")
    Page<Long> getDistinctRecentlyPlayedSongs(String createdBy, PageRequest pageRequest);

    @Query("SELECT p.song.id " +
            "FROM PlaybackInfoEntity p " +
            "WHERE p.createdBy = ?1 AND p.playCount > 0 " +
            "GROUP BY p.song.id " +
            "ORDER BY MAX(p.createdDate) DESC")
    Page<ArtistEntity> getDistinctRecentlyPlayedSongsArtist(String createdBy, PageRequest pageRequest);
}
