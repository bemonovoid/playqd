package com.bemonovoid.playqd.core.dao;

import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.FindArtistsRequest;
import com.bemonovoid.playqd.core.model.MoveResult;
import com.bemonovoid.playqd.core.model.PageableResult;

public interface ArtistDao {

    Artist getOne(long id);

    PageableResult<Artist> getAll(FindArtistsRequest request);

    void setSpotifyArtistId(long artistId, String spotifyId);

    void update(Artist artist);

    MoveResult move(long fromArtistId, long toArtistId);

}
