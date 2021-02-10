package com.bemonovoid.playqd.controller;

import java.util.List;

import com.bemonovoid.playqd.core.model.AlbumSongs;
import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.model.query.AlbumSongsQuery;
import com.bemonovoid.playqd.core.model.query.SongQuery;
import com.bemonovoid.playqd.core.service.LibraryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.LIBRARY_API_BASE_PATH)
class SongController {

    private final LibraryService libraryService;

    SongController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping("/songs/{songId}")
    ResponseEntity<Song> getSong(@PathVariable long songId) {
        return libraryService.getSong(songId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/songs/album/{albumId}")
    AlbumSongs getAlbumSongs(@PathVariable long albumId) {
        return libraryService.getAlbumSongs(new AlbumSongsQuery(albumId));
    }

    @GetMapping("/songs")
    List<Song> librarySongs(SongQuery query) {
        return libraryService.getSongs(query);
    }

    @PostMapping("/songs/{songId}")
    void updateSongFavoriteStatus(@PathVariable long songId) {
        libraryService.updateSongFavoriteStatus(songId);
    }
}
