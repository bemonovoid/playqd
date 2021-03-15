package com.bemonovoid.playqd.core.dao;

import java.util.List;

import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.BasicArtist;
import com.bemonovoid.playqd.core.model.MoveResult;
import com.bemonovoid.playqd.core.model.pageable.PageableRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;

public interface ArtistDao {

    Artist getOne(long id);

    List<BasicArtist> getAllBasicArtists();

    PageableResult<Artist> getArtists(PageableRequest pageableRequest);

    PageableResult<Artist> getRecentlyPlayedArtists(PageableRequest pageableRequest);

    PageableResult<Artist> getMostPlayedArtists(PageableRequest pageableRequest);

    PageableResult<Artist> getArtistsWithNameContaining(String name, PageableRequest pageableRequest);

    void setSpotifyArtistId(long artistId, String spotifyId);

    void update(Artist artist);

    MoveResult move(long fromArtistId, long toArtistId);

}
