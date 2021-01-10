package com.bemonovoid.playqd.library.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import com.bemonovoid.playqd.library.model.AlbumArtwork;
import com.bemonovoid.playqd.library.service.LibraryQueryService;
import com.bemonovoid.playqd.utils.Endpoints;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.ARTWORK_API_BASE_PATH)
class ArtworkController {

    private final LibraryQueryService libraryQueryService;

    ArtworkController(LibraryQueryService libraryQueryService) {
        this.libraryQueryService = libraryQueryService;
    }

    @GetMapping(value = "/open", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    ResponseEntity<byte[]> getArtwork(@RequestParam(defaultValue = "-1") long albumId,
                                      @RequestParam(defaultValue = "-1") long songId) {

        Optional<AlbumArtwork> mayBeArtwork;
        if (albumId > 0) {
            mayBeArtwork = libraryQueryService.getArtworkByAlbumId(albumId);
        } else if (songId > 0) {
            mayBeArtwork = libraryQueryService.getArtworkBySongId(songId);
        } else {
            return ResponseEntity.badRequest().build();
        }
        return mayBeArtwork
                .filter(AlbumArtwork::isAvailable)
                .map(albumArtwork -> ResponseEntity.ok(albumArtwork.getBinaryData()))
                .orElseGet(() -> ResponseEntity.ok(getDefault()));
    }

    private byte[] getDefault() {
        try (InputStream is = new ClassPathResource("/public/images/default-album-cover.png").getInputStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
