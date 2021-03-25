package com.bemonovoid.playqd.controller;

import java.util.List;

import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.model.UpdateOptions;
import com.bemonovoid.playqd.core.model.UpdateSong;
import com.bemonovoid.playqd.core.model.pageable.FindSongsRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableSongsResponse;
import com.bemonovoid.playqd.core.model.request.MoveSong;
import com.bemonovoid.playqd.core.service.SongService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Library Songs", description = "Songs resource")
@RestController
@RequestMapping(Endpoints.LIBRARY_API_BASE_PATH + "/songs")
class SongController {

    private final SongService songService;

    SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping
    PageableSongsResponse getSongs(FindSongsRequest request) {
        return new PageableSongsResponse(songService.getSongs(request));
    }

    @GetMapping("/album/{albumId}")
    PageableSongsResponse getAlbumSongs(@PathVariable String albumId,
                                        FindSongsRequest request) {
        return new PageableSongsResponse(songService.getAlbumSongs(albumId, request));
    }

    @GetMapping("/artist/{artistId}")
    PageableSongsResponse getArtistSongs(@PathVariable String artistId, FindSongsRequest request) {
        return new PageableSongsResponse(songService.getArtistSongs(artistId, request));
    }

    @GetMapping("/{songId}")
    ResponseEntity<Song> getSong(@PathVariable String songId) {
        return ResponseEntity.ok(songService.getSong(songId));
    }

    @GetMapping("/album/{albumId}/formats")
    List<String> getAlbumSongsFormats(@PathVariable String albumId) {
        return songService.getAlbumSongsFormats(albumId);
    }

    @PutMapping(path = "/{songId}", consumes =  MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Song updateSong(@PathVariable String songId, @RequestBody UpdateSong model) {
        Song song = new Song();
        song.setId(songId);
        song.setName(model.getName());
        song.setComment(model.getComment());
        song.setLyrics(model.getLyrics());
        song.setTrackId(model.getTrackId());
        UpdateOptions options = UpdateOptions.builder().updateAudioTags(model.isUpdateAudioTags()).build();
        return songService.updateSong(song, options);
    }

    @PutMapping("/{songId}/favorite")
    void setFavorite(@PathVariable String songId) {
        songService.updateFavoriteFlag(songId, true);
    }

    @DeleteMapping("/{songId}/favorite")
    void unsetFavorite(@PathVariable String songId) {
        songService.updateFavoriteFlag(songId, false);
    }

    @PutMapping("/{songId}/played")
    void played(@PathVariable String songId) {
        songService.updatePlayCount(songId);
    }

    @PutMapping(path = "/{songId}/moved", consumes =  MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    void moveSong(@PathVariable String songId, @RequestBody MoveSong model) {
        UpdateOptions updateOptions = UpdateOptions.builder().updateAudioTags(model.isUpdateAudioTags()).build();
        songService.moveSong(songId, model.getAlbumIdTo(), updateOptions);
    }

}
