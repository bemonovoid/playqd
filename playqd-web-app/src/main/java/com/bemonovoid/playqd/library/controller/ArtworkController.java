package com.bemonovoid.playqd.library.controller;

import java.util.Optional;

import com.bemonovoid.playqd.library.model.AlbumArtwork;
import com.bemonovoid.playqd.library.model.query.ArtworkQuery;
import com.bemonovoid.playqd.library.service.ArtworkService;
import com.bemonovoid.playqd.utils.Endpoints;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.ARTWORK_API_BASE_PATH)
class ArtworkController {

    private final ArtworkService artworkService;

    ArtworkController(ArtworkService artworkService) {
        this.artworkService = artworkService;
    }

    @GetMapping(value = "/open", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    ResponseEntity<byte[]> getArtwork(@RequestParam(defaultValue = "-1") long albumId,
                                      @RequestParam(defaultValue = "-1") long songId) {

        Optional<AlbumArtwork> mayBeArtwork;
        if (albumId > 0) {
            return ResponseEntity.ok(artworkService.get(ArtworkQuery.fromAlbumId(albumId)).getBinaryData());
        } else if (songId > 0) {
            return ResponseEntity.ok(artworkService.get(ArtworkQuery.fromSongId(songId)).getBinaryData());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}
