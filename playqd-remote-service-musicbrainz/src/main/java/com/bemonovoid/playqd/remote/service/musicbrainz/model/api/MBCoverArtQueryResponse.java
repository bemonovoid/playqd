package com.bemonovoid.playqd.remote.service.musicbrainz.model.api;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBCoverArtQueryResponse {

    private List<MBImage> images;

}
