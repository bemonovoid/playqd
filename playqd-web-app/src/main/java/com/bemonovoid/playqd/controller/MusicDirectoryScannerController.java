package com.bemonovoid.playqd.controller;

import com.bemonovoid.playqd.core.service.LibraryDirectoryScanner;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/library")
class MusicDirectoryScannerController {

    private final LibraryDirectoryScanner directoryScanner;

    MusicDirectoryScannerController(LibraryDirectoryScanner directoryScanner) {
        this.directoryScanner = directoryScanner;
    }

    @PatchMapping("/refresh")
    void scan() {
        directoryScanner.scan();
    }
}
