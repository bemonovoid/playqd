package com.bemonovoid.playqd.core.service;

import java.util.Optional;

import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.Image;
import com.bemonovoid.playqd.core.model.ImageSize;
import com.bemonovoid.playqd.core.model.UpdateOptions;
import com.bemonovoid.playqd.core.model.pageable.FindArtistsRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;

public interface ArtistService {

    Artist getArtist(String artistId);

    PageableResult<Artist> getAllBasicArtists();

    PageableResult<Artist> getArtists(FindArtistsRequest pageableRequest);

    Optional<Image> getImage(String artistId, ImageSize size, boolean findRemotely);

    Artist updateArtist(Artist artist, UpdateOptions updateOptions);

    Artist move(String fromArtistId, String toArtistId, UpdateOptions updateOptions);

}
