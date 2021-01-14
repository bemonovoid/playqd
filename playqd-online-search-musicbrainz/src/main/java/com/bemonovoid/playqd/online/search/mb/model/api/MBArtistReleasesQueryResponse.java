package com.bemonovoid.playqd.online.search.mb.model.api;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBArtistReleasesQueryResponse {

    private String id;
    private String name;
    private List<MBRelease> releases;

}
