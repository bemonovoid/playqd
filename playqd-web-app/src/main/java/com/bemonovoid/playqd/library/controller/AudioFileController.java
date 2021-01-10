package com.bemonovoid.playqd.library.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import com.bemonovoid.playqd.library.model.Song;
import com.bemonovoid.playqd.library.model.query.SongQuery;
import com.bemonovoid.playqd.library.service.LibraryQueryService;
import com.bemonovoid.playqd.library.service.LibraryDirectory;
import com.bemonovoid.playqd.utils.Endpoints;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(Endpoints.AUDIO_API_BASE_PATH)
class AudioFileController {

    private static final int BYTE_RANGE = 128; // increase the byterange from here

    private final LibraryDirectory libraryDirectory;
    private final LibraryQueryService libraryQueryService;

    AudioFileController(LibraryDirectory libraryDirectory, LibraryQueryService libraryQueryService) {
        this.libraryDirectory = libraryDirectory;
        this.libraryQueryService = libraryQueryService;
    }

    @GetMapping("/debug/{songId}")
    void debug(@PathVariable long songId) {
        libraryQueryService.getSong(new SongQuery(songId))
                .ifPresent(song -> {
                    try {
                        AudioFile audioFile = AudioFileIO.read(new File(song.getFileLocation()));
                        Tag tag = audioFile.getTag();
                        audioFile.getAudioHeader();
                        System.out.println(audioFile);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                });
    }

    @GetMapping("/open")
    Mono<ResponseEntity<byte[]>> openAudioFile(@RequestHeader(value = "Range", required = false) String range,
                                               @RequestParam long songId) {
        Song song = libraryQueryService.getSong(new SongQuery(songId)).get();
//        String location = new String(Base64.getDecoder().decode(fileLocation.getBytes(StandardCharsets.UTF_8)));
        return Mono.just(getContent(song.getFileLocation(), range));
    }

//    @GetMapping(value = "/open")
//    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
//    Flux<DataBuffer> openAudioFile(@RequestHeader(value = "Range", required = false) String range,
//                                   @RequestParam long songId,
//                                   ServerHttpResponse serverHttpResponse) {
//        Song song = libraryQueryService.getSong(new SongQuery(songId));
//        Long fileSize = Optional.ofNullable(song.getFileLocation())
//            .map(file -> Paths.get(song.getFileLocation()))
//            .map(this::sizeFromFile)
//            .orElse(0L);
//
//        String fileExtension = song.getFileLocation().substring(song.getFileLocation().lastIndexOf(".") + 1);
//
//        if (range == null) {
//            serverHttpResponse.setStatusCode(HttpStatus.OK);
//            serverHttpResponse.getHeaders().add(HttpHeaders.CONTENT_TYPE, String.format("audio/%s", fileExtension));
//            serverHttpResponse.getHeaders().add(HttpHeaders.CONTENT_LENGTH, fileSize.toString());
//
//            return DataBufferUtils.read(
//                new FileSystemResource(song.getFileLocation()),
//                serverHttpResponse.bufferFactory(),
//                    BYTE_RANGE);
//        }
//
//        return DataBufferUtils.read(
//            new FileSystemResource(song.getFileLocation()),
//            serverHttpResponse.bufferFactory(),
//                BYTE_RANGE);
//    }

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
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
            .header("Content-Type", contentTypePrefix+"/" + fileType)
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
