package com.bemonovoid.playqd.library.model;

import com.bemonovoid.playqd.data.entity.ArtworkStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Album {

    private Long id;
    private String name;
    private Artist artist;
    private String genre;
    private String year;
    private String totalTime;
    private ArtworkStatus artworkStatus;
}
