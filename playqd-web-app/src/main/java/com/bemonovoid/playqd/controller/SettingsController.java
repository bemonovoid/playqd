package com.bemonovoid.playqd.controller;

import com.bemonovoid.playqd.core.model.pageable.PageableScanLogResponse;
import com.bemonovoid.playqd.core.model.settings.LibrarySettings;
import com.bemonovoid.playqd.core.service.ScannerLogService;
import com.bemonovoid.playqd.core.service.SettingsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.SETTINGS_API_BASE_PATH)
class SettingsController {

    private final SettingsService settingsService;
    private final ScannerLogService scannerLogService;

    SettingsController(SettingsService settingsService, ScannerLogService scannerLogService) {
        this.settingsService = settingsService;
        this.scannerLogService = scannerLogService;
    }

    @PatchMapping("/library")
    @ResponseStatus(HttpStatus.ACCEPTED)
    void rescanLibrary(@RequestBody(required = false) LibrarySettings librarySettings) {
        settingsService.rescanLibrary(librarySettings);
    }

    @PutMapping("/library")
    void updateLibrarySettings(@RequestBody LibrarySettings librarySettings) {
        settingsService.saveLibrarySettings(librarySettings);
    }

    @GetMapping("/library")
    LibrarySettings getLibrarySettings() {
        return settingsService.getLibrarySettings();
    }

    @GetMapping("/library/scans")
    PageableScanLogResponse getScannerLogs(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        return new PageableScanLogResponse(scannerLogService.getScannerLogs(page, size));
    }
}
