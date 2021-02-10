package com.bemonovoid.playqd.controller;

import com.bemonovoid.playqd.core.model.Artists;
import com.bemonovoid.playqd.core.service.LibraryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.LIBRARY_API_BASE_PATH)
class ArtistController {

    private final LibraryService libraryService;

    public ArtistController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping("/artists")
    Artists listArtists() {
        return libraryService.getArtists();
    }
}
