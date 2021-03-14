package com.bemonovoid.playqd.core.service.impl;

import java.util.Collections;
import java.util.List;

import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.Image;
import com.bemonovoid.playqd.core.service.ImageSearchService;
import org.springframework.stereotype.Component;

@Component
class EmptyImageSearchService implements ImageSearchService {

    @Override
    public List<Image> searchAlbumImage(Album album) {
        return Collections.emptyList();
    }

    @Override
    public List<Image> searchArtistImage(Artist artist) {
        return Collections.emptyList();
    }
}
