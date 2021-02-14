package com.bemonovoid.playqd.core.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Artwork {

    private String src;
    private String mimeType;
    private byte[] binary;
}
