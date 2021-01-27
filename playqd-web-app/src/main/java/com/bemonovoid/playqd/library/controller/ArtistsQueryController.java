package com.bemonovoid.playqd.library.controller;

import java.util.Optional;

import com.bemonovoid.playqd.library.model.Album;
import com.bemonovoid.playqd.library.model.Albums;
import com.bemonovoid.playqd.library.model.Artists;
import com.bemonovoid.playqd.library.model.query.AlbumsQuery;
import com.bemonovoid.playqd.library.service.LibraryQueryService;
import com.bemonovoid.playqd.utils.Endpoints;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.LIBRARY_API_BASE_PATH)
class ArtistsQueryController {

    private final LibraryQueryService libraryQueryService;

    public ArtistsQueryController(LibraryQueryService libraryQueryService) {
        this.libraryQueryService = libraryQueryService;
    }

    @GetMapping("/artists")
    Artists listArtists() {
        return libraryQueryService.getArtists();
    }

    @GetMapping("/albums")
    Albums listArtistAlbums(AlbumsQuery query) {
        return libraryQueryService.getAlbums(query);
    }

    @GetMapping("/albums/{albumId}")
    ResponseEntity<Album> getOne(@PathVariable long albumId) {
        Optional<Album> albumOpt = libraryQueryService.getAlbum(albumId);
        if (albumOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(albumOpt.get());
    }
}
