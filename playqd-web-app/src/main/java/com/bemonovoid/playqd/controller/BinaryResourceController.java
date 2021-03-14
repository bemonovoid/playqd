package com.bemonovoid.playqd.controller;

import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.bemonovoid.playqd.core.exception.PlayqdUnsupportedAudioFormatException;
import com.bemonovoid.playqd.core.helpers.ResourceIdHelper;
import com.bemonovoid.playqd.core.model.Image;
import com.bemonovoid.playqd.core.model.ImageSize;
import com.bemonovoid.playqd.core.model.LibraryResourceId;
import com.bemonovoid.playqd.core.model.ResourceTarget;
import com.bemonovoid.playqd.core.service.AlbumService;
import com.bemonovoid.playqd.core.service.ArtistService;
import com.bemonovoid.playqd.core.service.SongService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Library Binary Resource", description = "Images and Audio files")
@RestController
@RequestMapping(Endpoints.LIBRARY_API_BASE_PATH + "/resource")
class BinaryResourceController {

    private final ArtistService artistService;
    private final AlbumService albumService;
    private final SongService songService;

    BinaryResourceController(ArtistService artistService, AlbumService albumService, SongService songService) {
        this.artistService = artistService;
        this.albumService = albumService;
        this.songService = songService;
    }

    @GetMapping(path = "/image", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    ResponseEntity<byte[]> getArtistImage(@RequestParam String resourceId,
                                          @RequestParam(defaultValue = "SMALL") ImageSize size) {
        LibraryResourceId libraryResourceId = ResourceIdHelper.decode(resourceId);
        Optional<Image> imageOpt = Optional.empty();
        if (ResourceTarget.ARTIST == libraryResourceId.getTarget()) {
            imageOpt = artistService.getImage(libraryResourceId.getId(), size, false);
        } else if (ResourceTarget.ALBUM == libraryResourceId.getTarget()) {
            imageOpt = albumService.getImage(libraryResourceId.getId(), size, false);
        } else {
            return ResponseEntity.badRequest().build();
        }
        return imageOpt
                .map(image -> ResponseEntity
                        .ok()
                        .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
                        .body(image.getData()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * // See: Spring's AbstractMessageConverterMethodProcessor implementation that handles byte ranges
     * @param resourceId
     * @return Audio file resource at the given byte range.
     */
    @GetMapping("/audio")
    ResponseEntity<Resource> openAudioFile(@RequestParam String resourceId) {

        LibraryResourceId libraryResourceId = ResourceIdHelper.decode(resourceId);

        if (ResourceTarget.SONG != libraryResourceId.getTarget()) {
            return ResponseEntity.badRequest().build();
        }
        String fileLocation = songService.getSongFileLocation(libraryResourceId.getId());
        String fileType = fileLocation.substring(fileLocation.lastIndexOf(".") + 1);
        if ("mp3".equalsIgnoreCase(fileType)) {
            fileType = "mpeg";
        } else if ("wma".equalsIgnoreCase(fileType)) {
            throw new PlayqdUnsupportedAudioFormatException("'wma' audio format is not supported");
        } else if ("oga".equalsIgnoreCase(fileType)) {
            fileType = "ogg";
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "audio/" + fileType)
                .body(new FileSystemResource(Paths.get(fileLocation)));
    }

}
