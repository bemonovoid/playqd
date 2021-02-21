package com.bemonovoid.playqd.remote.service.spotify.model.api;

import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
public class SpotifyArtistAlbumsResponse {

    private List<SpotifyLibraryItem> items;

}
