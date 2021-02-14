package com.bemonovoid.playqd.core.dao;

import java.util.List;

import com.bemonovoid.playqd.core.model.Artist;

public interface ArtistDao {

    Artist getOne(long id);

    List<Artist> getAll();

    void updateArtist(Artist artist);

    void move(long artistIdFrom, long artistIdTo);

}
