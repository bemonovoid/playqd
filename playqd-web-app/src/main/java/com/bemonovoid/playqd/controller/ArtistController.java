package com.bemonovoid.playqd.controller;

import java.util.List;
import java.util.Optional;

import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.Image;
import com.bemonovoid.playqd.core.model.ImageInfo;
import com.bemonovoid.playqd.core.model.ImageSize;
import com.bemonovoid.playqd.core.model.UpdateArtist;
import com.bemonovoid.playqd.core.model.UpdateArtistGroup;
import com.bemonovoid.playqd.core.model.UpdateOptions;
import com.bemonovoid.playqd.core.model.pageable.FindArtistsRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableArtistsResponse;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;
import com.bemonovoid.playqd.core.model.request.MoveArtist;
import com.bemonovoid.playqd.core.service.ArtistService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Library Artists", description = "Artists resource")
@RestController
@RequestMapping(Endpoints.LIBRARY_API_BASE_PATH + "/artists")
class ArtistController {

    private final ArtistService artistService;

    ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    PageableArtistsResponse getArtists(FindArtistsRequest request) {
        PageableResult<Artist> pageableResult = artistService.getArtists(request);
        return new PageableArtistsResponse(pageableResult);
    }

    @GetMapping("/{artistId}")
    Artist getArtist(@PathVariable String artistId) {
        return artistService.getArtist(artistId);
    }

    @GetMapping("/view/basic")
    PageableArtistsResponse getAllArtistNames() {
        return new PageableArtistsResponse(artistService.getAllBasicArtists());
    }

    @GetMapping("/{artistId}/images")
    ResponseEntity<List<Image>> getArtistImageSrc(@PathVariable String artistId) {
        List<Image> images = artistService.findImages(artistId);
        if (images.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(images);
    }

    @PutMapping("/{artistId}/images")
    void addImages(@PathVariable String artistId, @RequestBody List<ImageInfo> images) {
        artistService.addImages(artistId, images);
    }

    @PatchMapping("/{artistId}")
    Artist updateArtistDetails(@PathVariable String artistId, @RequestBody UpdateArtist updateArtist) {
        Artist artist = Artist.builder()
                .id(artistId)
                .spotifyId(updateArtist.getSpotifyArtistId())
                .name(updateArtist.getName())
                .country(updateArtist.getCountry())
                .build();
        UpdateOptions updateOptions = UpdateOptions.builder().updateAudioTags(updateArtist.isUpdateAudioTags()).build();
        return artistService.updateArtist(artist, updateOptions);
    }

    @PatchMapping("/group")
    void updateGroupOfArtists(@RequestBody UpdateArtistGroup artistsGroup) {

    }

    @PutMapping("/moved")
    Artist moveArtist(@RequestBody MoveArtist model) {
        UpdateOptions updateOptions = UpdateOptions.builder().updateAudioTags(model.isUpdateAudioTags()).build();
        return artistService.move(model.getArtistIdFrom(), model.getArtistIdTo(), updateOptions);
    }
}
