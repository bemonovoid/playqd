package com.bemonovoid.playqd.datasource.jdbc.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.bemonovoid.playqd.core.exception.PlayqdEntityNotFoundException;
import com.bemonovoid.playqd.datasource.jdbc.entity.SongEntity;
import com.bemonovoid.playqd.datasource.jdbc.projection.CountProjection;
import com.bemonovoid.playqd.datasource.jdbc.projection.FileLocationProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SongRepository extends JpaRepository<SongEntity, UUID> {

    default SongEntity findOne(UUID id) {
        return findById(id).orElseThrow(() -> new PlayqdEntityNotFoundException(id.toString(), "song"));
    }

    default Map<UUID, CountProjection> getArtistAlbumSongCount() {
        return getAlbumSongCount().stream().collect(Collectors.toMap(CountProjection::getArtistId, p -> p));
    }

    default Page<SongEntity> findWithNameContaining(String name, Pageable pageable) {
        return findByNameIgnoreCaseOrFileNameIgnoreCaseContaining(name, name, pageable);
    }

    Optional<FileLocationProjection> findFirstByAlbumId(UUID albumId);

    Page<SongEntity> findByAlbumId(UUID albumId, Pageable pageable);

    Page<SongEntity> findByArtistId(UUID artistId, Pageable pageable);

    Page<SongEntity> findByAlbumIdAndFileExtension(UUID albumId, String fileExtension, Pageable pageable);

    Page<SongEntity> findByNameIgnoreCaseOrFileNameIgnoreCaseContaining(String name, String fileName, Pageable pageable);

    @Query("SELECT s.fileLocation FROM SongEntity s WHERE s.id = ?1")
    Optional<String> findSongFileLocation(UUID songId);

    @Query("SELECT s.fileLocation FROM SongEntity s WHERE s.album.id = ?1")
    List<String> findAlbumSongsFileLocations(UUID albumId);

    @Query("SELECT s.fileLocation FROM SongEntity s WHERE s.artist.id = ?1")
    List<String> findArtistSongsFileLocations(UUID artistId);

    @Query("SELECT DISTINCT s.fileExtension FROM SongEntity s WHERE s.album.id = ?1")
    List<String> findAlbumSongsDistinctFileExtensions(UUID albumId);
    
    @Query("SELECT s.artist.id as artistId, COUNT(DISTINCT s.album.id) as albumCount, COUNT(s.id) as songCount " +
            "FROM SongEntity s " +
            "GROUP BY s.artist.id " +
            "ORDER BY songCount DESC")
    List<CountProjection> getAlbumSongCount();

    //TODO implement methodname
    @Query("SELECT s FROM SongEntity s WHERE s.playCount > 0 ORDER BY s.playCount DESC")
    Page<SongEntity> findMostPlayedSongs(PageRequest page);

    //TODO implement methodname
    @Query("SELECT s FROM SongEntity s WHERE s.playCount > 0 ORDER BY s.lastModifiedDate DESC")
    Page<SongEntity> findRecentlyPlayedSongs(PageRequest page);

    //TODO implement methodname
    @Query("SELECT s FROM SongEntity s WHERE s.favorite = 1 ORDER BY s.name ASC")
    Page<SongEntity> findFavoriteSongs(PageRequest page);

}
