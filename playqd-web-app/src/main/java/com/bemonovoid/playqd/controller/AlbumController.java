package com.bemonovoid.playqd.controller;

import java.util.Optional;

import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.Albums;
import com.bemonovoid.playqd.core.model.UpdateAlbum;
import com.bemonovoid.playqd.core.model.query.AlbumsQuery;
import com.bemonovoid.playqd.core.service.AlbumService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.LIBRARY_API_BASE_PATH + "/albums")
class AlbumController {

    private final AlbumService albumService;

    AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    Albums getAlbums(AlbumsQuery query) {
        return albumService.getAlbums(query);
    }

    @GetMapping("/{albumId}")
    ResponseEntity<Album> getAlbum(@PathVariable long albumId) {
        Optional<Album> albumOpt = albumService.getAlbum(albumId);
        if (albumOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(albumOpt.get());
    }

    @GetMapping(path = "/{albumId}/image", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    ResponseEntity<byte[]> getAlbumImageData(@PathVariable long albumId) {
        return albumService.getImage(albumId, false)
                .map(image -> ResponseEntity.ok(image.getData()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{albumId}/image/src")
    ResponseEntity<String> getAlbumImageSrc(@PathVariable long albumId) {
        return ResponseEntity.ok("");
    }

    @PutMapping
    void updateAlbum(@RequestBody UpdateAlbum model) {
        albumService.updateAlbum(model);
    }

}
