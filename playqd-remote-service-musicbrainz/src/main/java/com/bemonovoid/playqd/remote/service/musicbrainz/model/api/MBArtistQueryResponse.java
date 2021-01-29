package com.bemonovoid.playqd.remote.service.musicbrainz.model.api;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBArtistQueryResponse {

    private int count;
    private List<MBArtist> artists;

}
