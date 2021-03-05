package com.bemonovoid.playqd.core.service;

import java.util.Optional;

import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.AlbumPreferences;
import com.bemonovoid.playqd.core.model.Albums;
import com.bemonovoid.playqd.core.model.Image;
import com.bemonovoid.playqd.core.model.ImageSize;
import com.bemonovoid.playqd.core.model.UpdateOptions;
import com.bemonovoid.playqd.core.model.query.AlbumsQuery;

public interface AlbumService {

    Optional<Album> getAlbum(long albumId);

    Albums getAlbums(AlbumsQuery query);

    Optional<Image> getImage(long albumId, ImageSize size, boolean findRemotely);

    void updateAlbum(Album album, UpdateOptions updateOptions);

    void updateAlbumPreferences(AlbumPreferences preferences);

    void moveAlbum(long albumIdFrom, long albumIdTo, UpdateOptions updateOptions);
}
