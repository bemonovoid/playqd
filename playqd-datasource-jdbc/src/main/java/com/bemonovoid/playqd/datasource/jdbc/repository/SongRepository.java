package com.bemonovoid.playqd.datasource.jdbc.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.bemonovoid.playqd.core.exception.PlayqdEntityNotFoundException;
import com.bemonovoid.playqd.datasource.jdbc.entity.SongEntity;
import com.bemonovoid.playqd.datasource.jdbc.projection.CountProjection;
import com.bemonovoid.playqd.datasource.jdbc.projection.FileLocationProjection;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SongRepository extends JpaRepository<SongEntity, Long> {

    default SongEntity findOne(long id) {
        return findById(id).orElseThrow(() -> new PlayqdEntityNotFoundException(id, "song"));
    }

    default Map<Long, CountProjection> getArtistAlbumSongCount() {
        return getAlbumSongCount().stream().collect(Collectors.toMap(CountProjection::getArtistId, p -> p));
    }

    Optional<FileLocationProjection> findFirstByAlbumId(long albumId);

    List<SongEntity> findAllByAlbumId(long albumId);

    List<FileLocationProjection> findByArtistId(long artistId);
    
    @Query("SELECT s.artist.id as artistId, COUNT(DISTINCT s.album.id) as albumCount, COUNT(s.id) as songCount " +
            "FROM SongEntity s " +
            "GROUP BY s.artist.id " +
            "ORDER BY songCount DESC")
    List<CountProjection> getAlbumSongCount();

    @Query("SELECT p.song FROM PlaybackInfoEntity p WHERE p.createdBy = ?1 ORDER BY p.playCount DESC")
    List<SongEntity> findTopPlayedSongs(String createdBy, PageRequest page);

    @Query("SELECT p.song FROM PlaybackInfoEntity p WHERE p.createdBy = ?1 ORDER BY p.lastModifiedDate DESC")
    List<SongEntity> findRecentlyPlayedSongs(String createdBy, PageRequest page);

    @Query("SELECT p.song FROM PlaybackInfoEntity p WHERE p.createdBy = ?1 AND p.favorite = 1 " +
            "ORDER BY p.song.name ASC")
    List<SongEntity> findFavoriteSongs(String createdBy, PageRequest page);

}
