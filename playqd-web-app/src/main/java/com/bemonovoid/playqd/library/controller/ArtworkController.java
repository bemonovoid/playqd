package com.bemonovoid.playqd.library.controller;

import java.util.Optional;

import com.bemonovoid.playqd.core.ArtworkLocalQuery;
import com.bemonovoid.playqd.core.ArtworkUrl;
import com.bemonovoid.playqd.library.service.ArtworkService;
import com.bemonovoid.playqd.online.search.ArtworkBinary;
import com.bemonovoid.playqd.utils.Endpoints;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.ARTWORK_API_BASE_PATH)
class ArtworkController {

    private final ArtworkService artworkService;

    ArtworkController(ArtworkService artworkService) {
        this.artworkService = artworkService;
    }

    @GetMapping(value = "/open", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    ResponseEntity<byte[]> getArtwork(ArtworkLocalQuery query) {

        Optional<ArtworkBinary> mayBeArtwork;
        if (query.getAlbumId() != null || query.getSongId() != null) {
            return ResponseEntity.ok(artworkService.getBinaryFromLocalLibrary(query).getBinaryData());
        }
         else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(path = "/search")
    ResponseEntity<ArtworkUrl> searchOnline(ArtworkLocalQuery query) {
        if (query.getAlbumId() > 0 || query.getSongId() > 0) {
            Optional<String> mayBeUrl = artworkService.searchOnline(query);
            if (mayBeUrl.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(ArtworkUrl.builder().url(mayBeUrl.get()).build());
        }
        return ResponseEntity.badRequest().build();
    }

}
