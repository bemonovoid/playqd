package com.bemonovoid.playqd.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Image {

    private final String url;
    private final byte[] data;
    private final Dimensions dimensions;

}
