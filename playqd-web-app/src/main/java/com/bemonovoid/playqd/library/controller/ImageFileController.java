package com.bemonovoid.playqd.library.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.bemonovoid.playqd.library.model.Album;
import com.bemonovoid.playqd.library.model.query.SongQuery;
import com.bemonovoid.playqd.library.service.LibraryQueryService;
import com.bemonovoid.playqd.utils.Endpoints;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.IMAGE_API_BASE_PATH)
class ImageFileController {

    private final LibraryQueryService libraryQueryService;

    ImageFileController(LibraryQueryService libraryQueryService) {
        this.libraryQueryService = libraryQueryService;
    }

    @GetMapping(value = "/open", produces = MediaType.IMAGE_JPEG_VALUE)
    byte[] getAlbumArt(@RequestParam(defaultValue = "-1") long albumId,
                       @RequestParam(defaultValue = "-1") long songId) throws IOException {
        String fileLocation = null;
        if (albumId > 0) {
            fileLocation = getImageLocation(libraryQueryService.getAlbum(albumId));
        } else if (songId > 0) {
            fileLocation = getImageLocation(libraryQueryService.getSong(new SongQuery(songId)).getAlbum());
        } else {
            return new byte[]{};
        }
        if (fileLocation == null) {
            return new byte[]{};
        }
        Path path = Paths.get(fileLocation);
        InputStream inputStream = Files.newInputStream(path);
        byte[] targetArray = new byte[inputStream.available()];
        inputStream.read(targetArray);
        return targetArray;
    }

    private String getImageLocation(Album album) {
        List<String> imageLocations = album.getAlbumArt().getImageLocations();
        if (!imageLocations.isEmpty()) {
            return imageLocations.get(0);
        }
        return null;
    }
}
