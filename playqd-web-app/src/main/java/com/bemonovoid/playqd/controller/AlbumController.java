package com.bemonovoid.playqd.controller;

import java.util.Optional;

import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.Albums;
import com.bemonovoid.playqd.core.model.query.AlbumsQuery;
import com.bemonovoid.playqd.core.service.LibraryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.LIBRARY_API_BASE_PATH)
class AlbumController {

    private final LibraryService libraryService;

    AlbumController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping("/albums")
    Albums listArtistAlbums(AlbumsQuery query) {
        return libraryService.getAlbums(query);
    }

    @GetMapping("/albums/{albumId}")
    ResponseEntity<Album> getOne(@PathVariable long albumId) {
        Optional<Album> albumOpt = libraryService.getAlbum(albumId);
        if (albumOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(albumOpt.get());
    }
}
