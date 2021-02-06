package com.bemonovoid.playqd.controller;

import com.bemonovoid.playqd.core.service.PlaybackHistoryService;
import com.bemonovoid.playqd.datasource.jdbc.repository.PlaybackHistoryRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.LIBRARY_API_BASE_PATH)
class PlaybackHistoryController {

    private final PlaybackHistoryService playbackHistoryService;
    private final PlaybackHistoryRepository playbackHistoryRepository;

    PlaybackHistoryController(PlaybackHistoryService playbackHistoryService,
                              PlaybackHistoryRepository playbackHistoryRepository) {
        this.playbackHistoryService = playbackHistoryService;
        this.playbackHistoryRepository = playbackHistoryRepository;
    }

    @PutMapping("/history/{songId}")
    void songListened(@PathVariable long songId) {
        playbackHistoryService.updatePlaybackHistoryWithSongEnded(songId);
    }
}
