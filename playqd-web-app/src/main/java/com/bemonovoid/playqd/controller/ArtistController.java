package com.bemonovoid.playqd.controller;

import com.bemonovoid.playqd.core.model.Artists;
import com.bemonovoid.playqd.core.model.UpdateArtist;
import com.bemonovoid.playqd.core.service.LibraryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.LIBRARY_API_BASE_PATH + "/artists")
class ArtistController {

    private final LibraryService libraryService;

    ArtistController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @PutMapping("/{artistId}")
    void updateArtist(@PathVariable long artistId, @RequestBody UpdateArtist updateArtist) {
        libraryService.updateArtist(artistId, updateArtist);
    }

    @GetMapping
    Artists listArtists() {
        return libraryService.getArtists();
    }
}
