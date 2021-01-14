package com.bemonovoid.playqd.data.dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.data.entity.SongEntity;

public interface SongDao {

    Optional<SongEntity> getOne(long id);

    List<SongEntity> getArtistSongs(long artistId);

    List<SongEntity> getAlbumSongs(long albumId);

    Optional<SongEntity> getFirstSongInAlbum(long albumId);

    SongEntity save(SongEntity songEntity);

    void saveAll(Collection<SongEntity> songEntities);
}
