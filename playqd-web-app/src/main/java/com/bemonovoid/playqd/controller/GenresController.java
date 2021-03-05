package com.bemonovoid.playqd.controller;

import com.bemonovoid.playqd.core.model.Genres;
import com.bemonovoid.playqd.core.service.LibraryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Genres", description = "Genres resource")
@RestController
@RequestMapping(Endpoints.LIBRARY_API_BASE_PATH + "/genres")
class GenresController {

    private final LibraryService libraryService;

    GenresController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping
    Genres listGenres() {
        return libraryService.getGenres();
    }
}
