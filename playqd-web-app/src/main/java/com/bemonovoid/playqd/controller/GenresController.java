package com.bemonovoid.playqd.controller;

import com.bemonovoid.playqd.core.model.Genres;
import com.bemonovoid.playqd.core.service.LibraryQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(Endpoints.LIBRARY_API_BASE_PATH)
class GenresController {

    private final LibraryQueryService libraryQueryService;

    GenresController(LibraryQueryService libraryQueryService) {
        this.libraryQueryService = libraryQueryService;
    }

    @GetMapping("/genres")
    Genres listGenres() {
        return libraryQueryService.getGenres();
    }
}
