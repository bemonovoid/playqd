package com.bemonovoid.playqd.library.model.musicbrainz;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBImage {

    private long id;
    private boolean back;
    private boolean front;
    private String image;
    private String comment;
    private MBThumbnail thumbnails;

}
