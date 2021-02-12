package com.bemonovoid.playqd.core.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
public class UpdateArtistRequest {

    private String name;
    private String country;
}
