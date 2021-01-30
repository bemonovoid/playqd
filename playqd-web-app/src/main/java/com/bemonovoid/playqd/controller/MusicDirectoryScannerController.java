package com.bemonovoid.playqd.controller;

import com.bemonovoid.playqd.core.service.MusicDirectoryScanner;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.LIBRARY_API_BASE_PATH)
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
