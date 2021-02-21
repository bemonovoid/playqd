package com.bemonovoid.playqd.core.dao;

import java.util.List;

import com.bemonovoid.playqd.core.model.Artist;

public interface ArtistDao {

    Artist getOne(long id);

    List<Artist> getAll();

    void setSpotifyArtistId(long artistId, String spotifyId);

    boolean update(Artist artist);

    void move(long fromArtistId, long toArtistId);

}
