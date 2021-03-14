package com.bemonovoid.playqd.controller;

import com.bemonovoid.playqd.core.model.pageable.FindGenresRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableGenresResponse;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;
import com.bemonovoid.playqd.core.service.GenresService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Library Genres", description = "Genres resource")
@RestController
@RequestMapping(Endpoints.LIBRARY_API_BASE_PATH + "/genres")
class GenresController {

    private final GenresService genresService;

    GenresController(GenresService genresService) {
        this.genresService = genresService;
    }

    @GetMapping
    PageableGenresResponse getGenres(FindGenresRequest request) {
        PageableResult<String> genres = genresService.getGenres(request);
        return new PageableGenresResponse(genres);
    }

}
