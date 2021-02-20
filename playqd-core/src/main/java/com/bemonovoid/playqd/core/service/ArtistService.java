package com.bemonovoid.playqd.core.service;

import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.Image;
import com.bemonovoid.playqd.core.model.ImageSize;
import com.bemonovoid.playqd.core.model.UpdateArtist;

public interface ArtistService {

    List<Artist> getArtists();

    Optional<Image> getImage(long artistId, ImageSize size, boolean findRemotely);

    Artist move( long fromArtistId, long toArtistId);

    Artist updateArtist(UpdateArtist updateArtist);
}
