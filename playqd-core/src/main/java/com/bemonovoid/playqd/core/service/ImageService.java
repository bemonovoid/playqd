package com.bemonovoid.playqd.core.service;

import java.util.Optional;

import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.Image;
import com.bemonovoid.playqd.core.model.ImageSize;

public interface ImageService {

//    Artwork getAlbumArtworkFromLibrary(ArtworkLocalSearchQuery localQuery);

//    Optional<String> getAlbumArtworkOnline(ArtworkLocalSearchQuery localQuery);

//    void updateAlbumArtwork(long albumId, String resourceUrl);

    Optional<Image> getAlbumImage(Album album, boolean findRemotely);

    Optional<Image> getArtistImage(Artist artist, ImageSize size, boolean findRemotely);
}