package com.bemonovoid.playqd.core.service;

import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.BasicArtist;
import com.bemonovoid.playqd.core.model.Image;
import com.bemonovoid.playqd.core.model.ImageSize;
import com.bemonovoid.playqd.core.model.UpdateOptions;
import com.bemonovoid.playqd.core.model.pageable.FindArtistsRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;

public interface ArtistService {

    List<BasicArtist> getAllBasicArtists();

    PageableResult<Artist> getArtists(FindArtistsRequest pageableRequest);

    Optional<Image> getImage(long artistId, ImageSize size, boolean findRemotely);

    Artist updateArtist(Artist artist, UpdateOptions updateOptions);

    Artist move( long fromArtistId, long toArtistId, UpdateOptions updateOptions);

}
