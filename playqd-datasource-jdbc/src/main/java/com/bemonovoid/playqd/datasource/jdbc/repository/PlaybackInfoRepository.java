package com.bemonovoid.playqd.datasource.jdbc.repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.bemonovoid.playqd.datasource.jdbc.entity.PlaybackInfoEntity;
import com.bemonovoid.playqd.datasource.jdbc.projection.PlaybackInfoArtistProjection;
import com.bemonovoid.playqd.datasource.jdbc.projection.PlaybackInfoSongProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PlaybackInfoRepository extends JpaRepository<PlaybackInfoEntity, Long> {

    default Map<Long, PlaybackInfoSongProjection> findAlbumSongsPlaybackInfo(long albumId, String createdBy) {
        return playbackInfoGroupedByAlbumSongs(createdBy, albumId).stream()
                .collect(Collectors.toMap(PlaybackInfoSongProjection::getSongId, v -> v));
    }

    @Query("SELECT p.song.artist.id AS artistId, COUNT(p.song.id) AS playCount, MAX(p.createdDate) AS mostRecentPlayDateTime " +
            "FROM PlaybackInfoEntity p " +
            "WHERE p.createdBy = ?1 " +
            "GROUP BY artistId " +
            "ORDER BY mostRecentPlayDateTime DESC")
    List<PlaybackInfoArtistProjection> playbackInfoGroupedByArtist(String createdBy);

    @Query("SELECT p.song.id AS songId, COUNT(p.song.id) AS playCount, MAX(p.createdDate) AS mostRecentPlayDateTime " +
            "FROM PlaybackInfoEntity p " +
            "WHERE p.createdBy = ?1 " +
            "AND p.song.id IN (SELECT s.id FROM SongEntity s WHERE s.album.id = ?2) " +
            "GROUP BY songId")
    List<PlaybackInfoSongProjection> playbackInfoGroupedByAlbumSongs(String createdBy, long albumId);

//    @Query("SELECT p.song.id " +
//            "FROM PlaybackInfoEntity p " +
//            "WHERE p.createdBy = ?1 AND p.playCount > 0 " +
//            "GROUP BY p.song.id " +
//            "ORDER BY MAX(p.createdDate) DESC")
//    Page<Long> getDistinctRecentlyPlayedSongs(String createdBy, PageRequest pageRequest);
//
//    @Query("SELECT p.song.id " +
//            "FROM PlaybackInfoEntity p " +
//            "WHERE p.createdBy = ?1 AND p.playCount > 0 " +
//            "GROUP BY p.song.id " +
//            "ORDER BY MAX(p.createdDate) DESC")
//    Page<ArtistEntity> getDistinctRecentlyPlayedSongsArtist(String createdBy, PageRequest pageRequest);
}
