package com.bemonovoid.playqd.core.model;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
public class UpdateArtistGroup extends UpdateArtist {

    @NotEmpty
    private List<String> artistIds;
}
