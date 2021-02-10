package com.bemonovoid.playqd.datasource.jdbc.repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.bemonovoid.playqd.datasource.jdbc.entity.SongEntity;
import com.bemonovoid.playqd.datasource.jdbc.projection.CountProjection;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SongRepository extends JpaRepository<SongEntity, Long> {

    List<SongEntity> findAllByArtistId(Long artistId);

    List<SongEntity> findAllByAlbumId(Long albumId);

    Optional<SongEntity> findFirstByAlbumId(long albumId);


    default Map<Long, CountProjection> getArtistAlbumSongCount() {
        return getAlbumSongCount().stream().collect(Collectors.toMap(CountProjection::getArtistId, p -> p));
    }

    @Query("SELECT s.artist.id as artistId, COUNT(DISTINCT s.album.id) as albumCount, COUNT(s.id) as songCount " +
            "FROM SongEntity s " +
            "GROUP BY s.artist.id " +
            "ORDER BY songCount DESC")
    List<CountProjection> getAlbumSongCount();

    @Query("SELECT s FROM SongEntity s " +
            "LEFT JOIN PlaybackHistoryEntity h ON s.id = h.song.id " +
            "GROUP BY s.id ORDER BY COUNT(h.song.id) DESC")
    List<SongEntity> findTopPlayedSongs(PageRequest page);

    @Query("SELECT s FROM SongEntity s " +
            "INNER JOIN FavoriteSongEntity f ON s.id = f.songId " +
            "ORDER BY s.name ASC")
    List<SongEntity> findFavoriteSongs(PageRequest page);

}
