package com.bemonovoid.playqd.controller;

import java.util.Optional;

import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.Artists;
import com.bemonovoid.playqd.core.model.Image;
import com.bemonovoid.playqd.core.model.ImageSize;
import com.bemonovoid.playqd.core.model.UpdateArtist;
import com.bemonovoid.playqd.core.service.ArtistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.LIBRARY_API_BASE_PATH + "/artists")
class ArtistController {

    private final ArtistService artistService;

    ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    Artists listArtists() {
        return new Artists(artistService.getArtists());
    }

    @GetMapping("/{artistId}/image")
    ResponseEntity<byte[]> getArtistImage(@PathVariable long artistId,
                                       @RequestParam(defaultValue = "SMALL") ImageSize size) {
        Optional<Image> artistImageOpt = artistService.getImage(artistId, size, false);
        return artistImageOpt
                .map(artistImage -> ResponseEntity.ok(artistImage.getData()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{artistId}/image/src")
    ResponseEntity<String> getArtistImageSrc(@PathVariable long artistId) {
        Optional<Image> artistImageOpt = artistService.getImage(artistId, ImageSize.SMALL, true);
        return artistImageOpt
                .map(artistImage -> ResponseEntity.ok(artistImage.getUrl()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping
    Artist updateArtist(@RequestBody UpdateArtist updateArtist) {
        if (updateArtist.getMoveToArtistId() == null) {
            return artistService.updateArtist(updateArtist);
        } else {
            return artistService.move(updateArtist.getId(), updateArtist.getMoveToArtistId());
        }
    }
}
