package com.bemonovoid.playqd.core.service;

import java.util.List;

import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.Image;
import com.bemonovoid.playqd.core.model.ImageInfo;
import com.bemonovoid.playqd.core.model.UpdateOptions;
import com.bemonovoid.playqd.core.model.pageable.FindArtistsRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;

public interface ArtistService {

    void addImages(String artistId, List<ImageInfo> images);

    Artist getArtist(String artistId);

    PageableResult<Artist> getAllBasicArtists();

    PageableResult<Artist> getArtists(FindArtistsRequest pageableRequest);

    List<Image> findImages(String artistId);

    Artist updateArtist(Artist artist, UpdateOptions updateOptions);

    Artist move(String fromArtistId, String toArtistId, UpdateOptions updateOptions);

}
