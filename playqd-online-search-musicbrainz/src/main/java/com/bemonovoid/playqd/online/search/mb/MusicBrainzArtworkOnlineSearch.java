package com.bemonovoid.playqd.online.search.mb;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.bemonovoid.playqd.online.search.ArtworkOnlineSearchService;
import com.bemonovoid.playqd.online.search.ArtworkSearchFilter;
import com.bemonovoid.playqd.online.search.ArtworkSearchResult;
import com.bemonovoid.playqd.online.search.mb.model.MBQueryContext;
import com.bemonovoid.playqd.online.search.mb.model.api.MBArtist;
import com.bemonovoid.playqd.online.search.mb.model.api.MBArtistQueryResponse;
import com.bemonovoid.playqd.online.search.mb.model.api.MBArtistReleasesQueryResponse;
import com.bemonovoid.playqd.online.search.mb.model.api.MBCoverArtQueryResponse;
import com.bemonovoid.playqd.online.search.mb.model.api.MBImage;
import com.bemonovoid.playqd.online.search.mb.model.api.MBRelease;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MusicBrainzArtworkOnlineSearch implements ArtworkOnlineSearchService {

    private static final Logger LOG = LoggerFactory.getLogger(MusicBrainzArtworkOnlineSearch.class);

    private final MusicBrainzApiClient musicBrainzApiClient;
    private final MusicBrainzCoverArtApiClient coverArtApiClient;

    public MusicBrainzArtworkOnlineSearch(MusicBrainzApiClient musicBrainzApiClient,
                                          MusicBrainzCoverArtApiClient coverArtApiClient) {
        this.musicBrainzApiClient = musicBrainzApiClient;
        this.coverArtApiClient = coverArtApiClient;
    }

    @Override
    public synchronized Optional<ArtworkSearchResult> search(ArtworkSearchFilter searchFilter) {

        LOG.info("Searching album art with filter: {}", searchFilter);

        if (searchFilter.getMbArtistId() != null && !searchFilter.getMbArtistId().isBlank()) {
            return getArtworkInfo(MBQueryContext.builder()
                    .mbArtistId(searchFilter.getMbArtistId())
                    .albumName(searchFilter.getAlbumName())
                    .build());
        } else {
            Optional<MBArtistQueryResponse> artistOpt =
                    musicBrainzApiClient.getArtist(searchFilter.getArtistName());

            if (artistOpt.isEmpty()) {
                return Optional.empty();
            }

            MBArtistQueryResponse mbArtistResponse = artistOpt.get();

            if (mbArtistResponse.getCount() == 0 || mbArtistResponse.getArtists().isEmpty()) {
                return Optional.empty();
            }

            for (MBArtist mbArtist : mbArtistResponse.getArtists()) {
                Optional<ArtworkSearchResult> artworkInfoOpt = getArtworkInfo(MBQueryContext.builder()
                        .mbArtistId(mbArtist.getId())
                        .mbArtistCountry(mbArtist.getCountry())
                        .albumName(searchFilter.getAlbumName())
                        .build());
                if (artworkInfoOpt.isEmpty()) {
                    continue;
                }
                return artworkInfoOpt;
            }

            return Optional.empty();
        }
    }

    private Optional<ArtworkSearchResult> getArtworkInfo(MBQueryContext mbQueryContext) {

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

        LOG.info("Available release titles: {}", String.join(",", allTitles));

        if (releaseOpt.isEmpty()) {
            LOG.warn("No release with name like '{}' was found", albumName);
            return Optional.empty();
        }

        MBRelease release = releaseOpt.get();

        LOG.info("Release matching '{}' found. Id: {}", albumName, release.getId());

        Optional<MBCoverArtQueryResponse> coverArtOpt = coverArtApiClient.getCoverArt(release.getId());

        if (coverArtOpt.isPresent()) {
            Optional<String> coverArtUrlOpt = getFront500pxCoverArtUrl(coverArtOpt.get());
            if (coverArtUrlOpt.isEmpty()) {
                LOG.warn("artwork 500px format is not available");
                coverArtUrlOpt = getDefaultFrontCoverArtUrl(coverArtOpt.get());
            }
            if (coverArtUrlOpt.isEmpty()) {
                LOG.warn("artwork default format is not available. No artwork image will be provided.");
                return Optional.empty();
            }

            LOG.info("Artwork was successfully found.");

            return Optional.of(ArtworkSearchResult.builder()
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