package com.bemonovoid.playqd.controller;

import com.bemonovoid.playqd.core.service.PlaybackHistoryService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.LIBRARY_API_BASE_PATH)
class PlaybackHistoryController {

    private final PlaybackHistoryService playbackHistoryService;

    PlaybackHistoryController(PlaybackHistoryService playbackHistoryService) {
        this.playbackHistoryService = playbackHistoryService;
    }

    @PutMapping("/history/{songId}")
    void songListened(@PathVariable long songId) {
        playbackHistoryService.updatePlaybackHistoryWithSongEnded(songId);
    }
}
