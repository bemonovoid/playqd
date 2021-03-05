package com.bemonovoid.playqd.controller;

import com.bemonovoid.playqd.core.service.MusicDatabaseBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Music Database", description = "Music Database resource")
@RestController
@RequestMapping(Endpoints.LIBRARY_API_BASE_PATH + "/directory")
class MusicDatabaseController {

    private final MusicDatabaseBuilder databaseBuilder;

    MusicDatabaseController(MusicDatabaseBuilder databaseBuilder) {
        this.databaseBuilder = databaseBuilder;
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    void refresh(@RequestParam(defaultValue = "false") boolean drop) {
        databaseBuilder.build(drop);
    }

}
