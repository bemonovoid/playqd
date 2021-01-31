package com.bemonovoid.playqd.controller;

import java.util.Optional;

import com.bemonovoid.playqd.core.model.ArtworkUrl;
import com.bemonovoid.playqd.core.model.query.ArtworkLocalSearchQuery;
import com.bemonovoid.playqd.core.service.ArtworkService;
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
    ResponseEntity<byte[]> getArtwork(ArtworkLocalSearchQuery query) {

        if (query.getAlbumId() != null || query.getSongId() != null) {
            return ResponseEntity.ok(artworkService.getArtworkFromLibrary(query).getBinary());
        }
         else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(path = "/search")
    ResponseEntity<ArtworkUrl> searchOnline(ArtworkLocalSearchQuery query) {
//        if (query.getAlbumId() > 0 || query.getSongId() > 0) {
//            Optional<String> mayBeUrl = artworkService.getArtworkOnline(query);
//            if (mayBeUrl.isEmpty()) {
//                return ResponseEntity.notFound().build();
//            }
//            return ResponseEntity.ok(ArtworkUrl.builder().url(mayBeUrl.get()).build());
//        }
//        return ResponseEntity.badRequest().build();
        return ResponseEntity.notFound().build();
    }

}
