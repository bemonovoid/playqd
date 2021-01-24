package com.bemonovoid.playqd.library.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Albums {

    private List<Album> albums;
}
