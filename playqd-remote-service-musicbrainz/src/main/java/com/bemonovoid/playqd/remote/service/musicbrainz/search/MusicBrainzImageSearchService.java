package com.bemonovoid.playqd.remote.service.musicbrainz.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.bemonovoid.playqd.core.model.ArtworkOnlineSearchResult;
import com.bemonovoid.playqd.core.model.query.ArtworkOnlineSearchQuery;
import com.bemonovoid.playqd.remote.service.musicbrainz.model.MBQueryContext;
import com.bemonovoid.playqd.remote.service.musicbrainz.model.api.MBArtist;
import com.bemonovoid.playqd.remote.service.musicbrainz.model.api.MBArtistQueryResponse;
import com.bemonovoid.playqd.remote.service.musicbrainz.model.api.MBArtistReleasesQueryResponse;
import com.bemonovoid.playqd.remote.service.musicbrainz.model.api.MBCoverArtQueryResponse;
import com.bemonovoid.playqd.remote.service.musicbrainz.model.api.MBImage;
import com.bemonovoid.playqd.remote.service.musicbrainz.model.api.MBRelease;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class MusicBrainzImageSearchService {

    private final MusicBrainzApiClient musicBrainzApiClient;
    private final MusicBrainzCoverArtApiClient coverArtApiClient;

    MusicBrainzImageSearchService(MusicBrainzApiClient musicBrainzApiClient,
                                  MusicBrainzCoverArtApiClient coverArtApiClient) {
        this.musicBrainzApiClient = musicBrainzApiClient;
        this.coverArtApiClient = coverArtApiClient;
    }

    public synchronized Optional<ArtworkOnlineSearchResult> search(ArtworkOnlineSearchQuery onlineSearchQuery) {

        log.info("Searching album art by query: {}", onlineSearchQuery);

        if (onlineSearchQuery.getMbArtistId() != null && !onlineSearchQuery.getMbArtistId().isBlank()) {
            return searchInternal(MBQueryContext.builder()
                    .mbArtistId(onlineSearchQuery.getMbArtistId())
                    .albumName(onlineSearchQuery.getAlbumName())
                    .build());
        } else {
            Optional<MBArtistQueryResponse> artistOpt =
                    musicBrainzApiClient.getArtist(onlineSearchQuery.getArtistName());

            if (artistOpt.isEmpty()) {
                return Optional.empty();
            }

            MBArtistQueryResponse mbArtistResponse = artistOpt.get();

            if (mbArtistResponse.getCount() == 0 || mbArtistResponse.getArtists().isEmpty()) {
                return Optional.empty();
            }

            for (MBArtist mbArtist : mbArtistResponse.getArtists()) {
                Optional<ArtworkOnlineSearchResult> searchResultOpt = searchInternal(MBQueryContext.builder()
                        .albumName(onlineSearchQuery.getAlbumName())
                        .mbArtistId(mbArtist.getId())
                        .mbArtistCountry(mbArtist.getCountry())
                        .build());
                if (searchResultOpt.isEmpty()) {
                    continue;
                }
                return searchResultOpt;
            }

            return Optional.empty();
        }
    }

    private Optional<ArtworkOnlineSearchResult> searchInternal(MBQueryContext mbQueryContext) {

        Optional<MBArtistReleasesQueryResponse> releasesOpt =
                musicBrainzApiClient.getReleases(mbQueryContext.getMbArtistId());

        if (releasesOpt.isEmpty()) {
            return Optional.empty();
        }

        MBArtistReleasesQueryResponse releases = releasesOpt.get();

        List<String> allTitles = new ArrayList<>(releases.getReleases().size());
        String albumName = mbQueryContext.getAlbumName();

        Optional<MBRelease> releaseOpt = releases.getReleases().stream()
                .peek(release -> allTitles.add(release.getTitle()))
                .filter(release -> {
                    String releaseTitle = release.getTitle().toLowerCase();
                    return albumName.equals(releaseTitle) || releaseTitle.startsWith(albumName);
                })
                .findFirst();

        log.info("Available release titles: {}", String.join(",", allTitles));

        if (releaseOpt.isEmpty()) {
            log.warn("No release with name like '{}' was found", albumName);
            return Optional.empty();
        }

        MBRelease release = releaseOpt.get();

        log.info("Release matching '{}' found. Id: {}", albumName, release.getId());

        Optional<MBCoverArtQueryResponse> coverArtOpt = coverArtApiClient.getCoverArt(release.getId());

        if (coverArtOpt.isPresent()) {
            Optional<String> coverArtUrlOpt = getFront500pxCoverArtUrl(coverArtOpt.get());
            if (coverArtUrlOpt.isEmpty()) {
                log.warn("artwork 500px format is not available");
                coverArtUrlOpt = getDefaultFrontCoverArtUrl(coverArtOpt.get());
            }
            if (coverArtUrlOpt.isEmpty()) {
                log.warn("artwork default format is not available. No artwork image will be provided.");
                return Optional.empty();
            }

            log.info("Artwork was successfully found.");

            return Optional.of(ArtworkOnlineSearchResult.builder()
                    .imageUrl(coverArtUrlOpt.get())
                    .mbArtistId(mbQueryContext.getMbArtistId())
                    .mbArtistCountry(mbQueryContext.getMbArtistCountry())
                    .mbReleaseId(release.getId())
                    .build());
        }
        return Optional.empty();
    }

    private Optional<String> getDefaultFrontCoverArtUrl(MBCoverArtQueryResponse coverArt) {
        return coverArt.getImages().stream()
                .filter(MBImage::isFront)
                .map(MBImage::getImage)
                .findFirst();
    }

    private Optional<String> getFront500pxCoverArtUrl(MBCoverArtQueryResponse coverArt) {
        return coverArt.getImages().stream()
                .filter(MBImage::isFront)
                .filter(mbImage -> Objects.nonNull(mbImage.getThumbnails().getFiveHundred()))
                .map(mbImage -> mbImage.getThumbnails().getFiveHundred())
                .findFirst();
    }

    private Optional<String> getFront250pxCoverArtUrl(MBCoverArtQueryResponse coverArt) {
        return coverArt.getImages().stream()
                .filter(MBImage::isFront)
                .filter(mbImage -> Objects.nonNull(mbImage.getThumbnails().getTwoHundredAndFifty()))
                .map(mbImage -> mbImage.getThumbnails().getTwoHundredAndFifty())
                .findFirst();
    }

}