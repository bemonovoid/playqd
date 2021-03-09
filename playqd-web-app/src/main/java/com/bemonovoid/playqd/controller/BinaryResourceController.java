package com.bemonovoid.playqd.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.bemonovoid.playqd.core.helpers.ResourceIdHelper;
import com.bemonovoid.playqd.core.model.Image;
import com.bemonovoid.playqd.core.model.ImageSize;
import com.bemonovoid.playqd.core.model.LibraryResourceId;
import com.bemonovoid.playqd.core.model.ResourceTarget;
import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.service.AlbumService;
import com.bemonovoid.playqd.core.service.ArtistService;
import com.bemonovoid.playqd.core.service.SongService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(Endpoints.LIBRARY_API_BASE_PATH + "/resource")
class BinaryResourceController {

    private static final int BYTE_RANGE = 128;

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

    @GetMapping("/audio")
    ResponseEntity<byte[]> openAudioFile(@RequestParam String resourceId,
                                         @RequestHeader(value = "Range", required = false) String range) {

        LibraryResourceId libraryResourceId = ResourceIdHelper.decode(resourceId);

        if (ResourceTarget.SONG != libraryResourceId.getTarget()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Song> songOpt = songService.getSong(libraryResourceId.getId());
        if (songOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return getContent(songOpt.get().getFileLocation(), range);
    }

    private ResponseEntity<byte[]> getContent(String fileName, String range) {
        return getContent(fileName, range, "audio");
    }

    private ResponseEntity<byte[]> getContent(String fileName, String range, String contentTypePrefix) {
        long rangeStart = 0;
        long rangeEnd;
        byte[] data;
        Long fileSize;
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
        try {
            fileSize = Optional.ofNullable(fileName)
                    .map(file -> Paths.get(fileName))
                    .map(this::sizeFromFile)
                    .orElse(0L);
            if (range == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .header("Content-Type", contentTypePrefix+"/" + fileType)
                        .header("Content-Length", String.valueOf(fileSize))
                        .body(readByteRange(fileName, rangeStart, fileSize - 1));
            }
            String[] ranges = range.split("-");
            rangeStart = Long.parseLong(ranges[0].substring(6));
            if (ranges.length > 1) {
                rangeEnd = Long.parseLong(ranges[1]);
            } else {
                rangeEnd = fileSize - 1;
            }
            if (fileSize < rangeEnd) {
                rangeEnd = fileSize - 1;
            }
            data = readByteRange(fileName, rangeStart, rangeEnd);
        } catch (IOException e) {
            log.error("File read error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header("Content-Type", contentTypePrefix + "/" + fileType)
                .header("Accept-Ranges", "bytes")
                .header("Content-Length", contentLength)
                .header("Content-Range", "bytes" + " " + rangeStart + "-" + rangeEnd + "/" + fileSize)
                .body(data);
    }

    public byte[] readByteRange(String filename, long start, long end) throws IOException {
        Path path = Paths.get(filename);
        try (InputStream inputStream = (Files.newInputStream(path));
             ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream()) {
            byte[] data = new byte[BYTE_RANGE];
            int nRead;
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                bufferedOutputStream.write(data, 0, nRead);
            }
            bufferedOutputStream.flush();
            byte[] result = new byte[(int) (end - start) + 1];
            System.arraycopy(bufferedOutputStream.toByteArray(), (int) start, result, 0, result.length);
            return result;
        }
    }

//    private String getFilePath(String fileName) {
//        return Paths.get(musicDirectory.basePath().toString(), fileName).toFile().getAbsolutePath();
//    }

    private Long sizeFromFile(Path path) {
        try {
            return Files.size(path);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return 0L;
    }
}
