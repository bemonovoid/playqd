package com.bemonovoid.playqd.library.controller;

import com.bemonovoid.playqd.library.service.MusicDirectoryScanner;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/playqd/library")
class MusicDirectoryScannerController {

    private final MusicDirectoryScanner directoryScanner;

    MusicDirectoryScannerController(MusicDirectoryScanner directoryScanner) {
        this.directoryScanner = directoryScanner;
    }

    @PatchMapping("/refresh")
    void scan() {
        directoryScanner.scan();
    }
}
