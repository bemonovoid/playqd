package com.bemonovoid.playqd.core.service;

import java.util.List;

import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.Image;

public interface ImageSearchService {

    List<Image> searchAlbumImage(Album album);

    List<Image> searchArtistImage(Artist artist);

}
