package com.bemonovoid.playqd.core.dao;

import java.util.List;

import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.ImageInfo;
import com.bemonovoid.playqd.core.model.MoveResult;
import com.bemonovoid.playqd.core.model.pageable.PageableRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;

public interface ArtistDao {

    Artist getOne(String id);

    PageableResult<Artist> getBasicArtists(PageableRequest pageableRequest);

    PageableResult<Artist> getArtists(PageableRequest pageableRequest);

    PageableResult<Artist> getRecentlyPlayedArtists(PageableRequest pageableRequest);

    PageableResult<Artist> getMostPlayedArtists(PageableRequest pageableRequest);

    PageableResult<Artist> getArtistsWithNameContaining(String name, PageableRequest pageableRequest);

    void update(Artist artist);

    void addImages(String artistId, List<ImageInfo> images);

    MoveResult move(String fromArtistId, String toArtistId);

}
