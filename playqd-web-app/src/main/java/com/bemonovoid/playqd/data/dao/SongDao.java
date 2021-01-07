package com.bemonovoid.playqd.data.dao;

import java.util.Collection;
import java.util.List;

import com.bemonovoid.playqd.data.entity.SongEntity;

public interface SongDao {

    SongEntity getOne(long id);

    List<SongEntity> getArtistSongs(long artistId);

    SongEntity save(SongEntity songEntity);

    void saveAll(Collection<SongEntity> songEntities);
}
