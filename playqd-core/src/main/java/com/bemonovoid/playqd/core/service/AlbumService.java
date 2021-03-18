package com.bemonovoid.playqd.core.service;

import java.util.Optional;

import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.AlbumPreferences;
import com.bemonovoid.playqd.core.model.Image;
import com.bemonovoid.playqd.core.model.ImageSize;
import com.bemonovoid.playqd.core.model.UpdateOptions;
import com.bemonovoid.playqd.core.model.pageable.FindAlbumRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;

public interface AlbumService {

    Optional<Album> getAlbum(String albumId);

    PageableResult<Album> getAlbums(FindAlbumRequest request);

    Optional<Image> getImage(String albumId, ImageSize size, boolean findRemotely);

    void updateAlbum(Album album, UpdateOptions updateOptions);

    void updateAlbumPreferences(String albumId, AlbumPreferences preferences);

    void moveAlbum(String albumIdFrom, String albumIdTo, UpdateOptions updateOptions);
}
