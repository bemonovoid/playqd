package com.bemonovoid.playqd.core.dao;

import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.model.Artist;

public interface ArtistDao {

    Artist getOne(Long id);

    Optional<Artist> getByName(String name);

    List<Artist> getAll();

    void updateArtist(Long artistId, String mbArtistId, String country);
}
