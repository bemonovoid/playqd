package com.bemonovoid.playqd.library.controller;

import com.bemonovoid.playqd.library.model.AlbumSongs;
import com.bemonovoid.playqd.library.model.Song;
import com.bemonovoid.playqd.library.model.query.AlbumSongsQuery;
import com.bemonovoid.playqd.library.model.query.SongQuery;
import com.bemonovoid.playqd.library.service.LibraryQueryService;
import com.bemonovoid.playqd.utils.Endpoints;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.LIBRARY_API_BASE_PATH)
class SongsQueryController {

    private final LibraryQueryService libraryQueryService;

    public SongsQueryController(LibraryQueryService libraryQueryService) {
        this.libraryQueryService = libraryQueryService;
    }

    @GetMapping("/songs/{songId}")
    ResponseEntity<Song> getSong(@PathVariable long songId) {
        return libraryQueryService.getSong(new SongQuery(songId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/songs/album/{albumId}")
    AlbumSongs getAlbumSongs(@PathVariable long albumId) {
        return libraryQueryService.getAlbumSongs(new AlbumSongsQuery(albumId));
    }
}
