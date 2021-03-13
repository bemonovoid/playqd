package com.bemonovoid.playqd.controller;

import java.util.Optional;

import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.AlbumPreferences;
import com.bemonovoid.playqd.core.model.Image;
import com.bemonovoid.playqd.core.model.ImageSize;
import com.bemonovoid.playqd.core.model.UpdateOptions;
import com.bemonovoid.playqd.core.model.pageable.FindAlbumRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableAlbumResponse;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;
import com.bemonovoid.playqd.core.model.UpdateAlbum;
import com.bemonovoid.playqd.core.model.request.MoveAlbum;
import com.bemonovoid.playqd.core.service.AlbumService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Albums", description = "Albums resource")
@RestController
@RequestMapping(Endpoints.LIBRARY_API_BASE_PATH + "/albums")
class AlbumController {

    private final AlbumService albumService;

    AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    PageableAlbumResponse getAlbums(FindAlbumRequest request) {
        PageableResult<Album> albums = albumService.getAlbums(request);
        return new PageableAlbumResponse(albums);
    }

    @GetMapping("/{albumId}")
    ResponseEntity<Album> getAlbum(@PathVariable long albumId) {
        Optional<Album> albumOpt = albumService.getAlbum(albumId);
        if (albumOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(albumOpt.get());
    }

    @GetMapping("/{albumId}/image/src")
    ResponseEntity<String> getAlbumImageSrc(@PathVariable long albumId) {
        Optional<Image> artistImageOpt = albumService.getImage(albumId, ImageSize.SMALL, true);
        return artistImageOpt
                .map(artistImage -> ResponseEntity.ok(artistImage.getUrl()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping
    void updateAlbum(@RequestBody UpdateAlbum model) {
        Album album = Album.builder()
                .id(model.getId())
                .name(model.getName())
                .date(model.getDate())
                .genre(model.getGenre())
                .build();
        UpdateOptions options = UpdateOptions.builder().updateAudioTags(model.isUpdateAudioTags()).build();
        albumService.updateAlbum(album, options);
    }

    @PutMapping("/preferences")
    void updateAlbumPreferences(@RequestBody AlbumPreferences preferences) {
        albumService.updateAlbumPreferences(preferences);
    }

    @PostMapping("/move")
    void moveAlbum(@RequestBody MoveAlbum model) {
        UpdateOptions updateOptions = UpdateOptions.builder().updateAudioTags(model.isUpdateAudioTags()).build();
        albumService.moveAlbum(model.getAlbumIdFrom(), model.getAlbumIdTo(), updateOptions);
    }

}
