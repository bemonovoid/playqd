package com.bemonovoid.playqd.core.dao;

import java.util.List;

import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.BasicArtist;
import com.bemonovoid.playqd.core.model.MoveResult;
import com.bemonovoid.playqd.core.model.pageable.FindArtistsRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;

public interface ArtistDao {

    Artist getOne(long id);

    List<BasicArtist> getAllBasicArtists();

    PageableResult<Artist> getAll(FindArtistsRequest request);

    void setSpotifyArtistId(long artistId, String spotifyId);

    void update(Artist artist);

    MoveResult move(long fromArtistId, long toArtistId);

}
