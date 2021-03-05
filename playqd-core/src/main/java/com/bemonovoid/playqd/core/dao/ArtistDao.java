package com.bemonovoid.playqd.core.dao;

import java.util.List;

import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.MoveResult;

public interface ArtistDao {

    Artist getOne(long id);

    List<Artist> getAll();

    void setSpotifyArtistId(long artistId, String spotifyId);

    void update(Artist artist);

    MoveResult move(long fromArtistId, long toArtistId);

}
