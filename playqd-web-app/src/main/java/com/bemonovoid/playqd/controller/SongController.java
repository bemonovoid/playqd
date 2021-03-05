package com.bemonovoid.playqd.controller;

import java.security.Principal;
import java.util.List;

import com.bemonovoid.playqd.core.model.AlbumSongs;
import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.model.query.AlbumSongsQuery;
import com.bemonovoid.playqd.core.model.query.SongQuery;
import com.bemonovoid.playqd.core.service.SongService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Songs", description = "Songs resource")
@RestController
@RequestMapping(Endpoints.LIBRARY_API_BASE_PATH + "/songs")
class SongController {

    private final SongService songService;

    SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping
    List<Song> librarySongs(SongQuery query) {
        return songService.getSongs(query);
    }

    @GetMapping("/{songId}")
    ResponseEntity<Song> getSong(@PathVariable long songId) {
        return songService.getSong(songId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/album/{albumId}")
    AlbumSongs getAlbumSongs(@PathVariable long albumId) {
        return songService.getAlbumSongs(new AlbumSongsQuery(albumId));
    }

    @PutMapping("/{songId}/favorite")
    void setFavorite(@PathVariable long songId) {
        songService.updateFavoriteFlag(songId, true);
    }

    @DeleteMapping("/{songId}/favorite")
    void unsetFavorite(@PathVariable long songId) {
        songService.updateFavoriteFlag(songId, false);
    }

    @PutMapping("/{songId}/stats/played")
    void played(@PathVariable long songId) {
        songService.updatePlayCount(songId);
    }

}
