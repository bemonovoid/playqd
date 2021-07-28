package com.bemonovoid.playqd.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Image {

    private final String url;
    private final byte[] data;
    private final Dimensions dimensions;

}
