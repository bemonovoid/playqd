package com.bemonovoid.playqd.core.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MoveResult {

    private Artist newArtist;
    private Album newAlbum;
    private List<String> movedSongFiles;
}
