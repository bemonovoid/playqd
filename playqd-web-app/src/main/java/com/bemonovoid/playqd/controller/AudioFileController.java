//package com.bemonovoid.playqd.controller;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Optional;
//
//import com.bemonovoid.playqd.core.helpers.ResourceIdHelper;
//import com.bemonovoid.playqd.core.model.Song;
//import com.bemonovoid.playqd.core.service.SongService;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.extern.slf4j.Slf4j;
//import org.jaudiotagger.audio.AudioFile;
//import org.jaudiotagger.audio.AudioFileIO;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@Tag(name = "Audio File", description = "Audio File resource")
//@Slf4j
//@RestController
//@RequestMapping(Endpoints.AUDIO_API_BASE_PATH)
//class AudioFileController {
//
//
//
//    AudioFileController(SongService songService) {
//        this.songService = songService;
//    }
//
//    @GetMapping("/debug/{songId}")
//    ResponseEntity<String> debug(@PathVariable long songId) {
//        Optional<String> opt = songService.getSong(songId)
//                .map(song -> {
//                    try {
//                        AudioFile audioFile = AudioFileIO.read(new File(song.getFileLocation()));
//                        return audioFile.toString();
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                });
//        return ResponseEntity.ok(opt.get());
//    }
//
//
//
//
//}
