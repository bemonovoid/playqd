package com.bemonovoid.playqd.online.search.mb;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.bemonovoid.playqd.online.search.ArtworkOnlineSearchService;
import com.bemonovoid.playqd.online.search.ArtworkSearchFilter;
import com.bemonovoid.playqd.online.search.ArtworkSearchResult;
import com.bemonovoid.playqd.online.search.mb.model.MBArtistQueryParams;
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
            return getArtworkInfo(new MBArtistQueryParams(searchFilter.getMbArtistId()), searchFilter.getAlbumName());
        } else {
            Optional<MBArtistQueryResponse> mayBeArtist = musicBrainzApiClient.getArtist(searchFilter.getArtistName());

            if (mayBeArtist.isEmpty()) {
                return Optional.empty();
            }

            MBArtistQueryResponse artist = mayBeArtist.get();

            if (artist.getCount() == 0 || artist.getArtists().isEmpty()) {
                return Optional.empty();
            }

            for (MBArtist mbArtist : artist.getArtists()) {
                Optional<ArtworkSearchResult> artworkInfo =
                        getArtworkInfo(new MBArtistQueryParams(mbArtist.getId()), searchFilter.getAlbumName());
                if (artworkInfo.isEmpty()) {
                    continue;
                }
                return artworkInfo;
            }

            return Optional.empty();
        }
    }

    private Optional<ArtworkSearchResult> getArtworkInfo(MBArtistQueryParams mbParams, String albumName) {

        Optional<MBArtistReleasesQueryResponse> releasesOpt = musicBrainzApiClient.getReleases(mbParams.getMbArtistId());

        if (releasesOpt.isEmpty()) {
            return Optional.empty();
        }

        MBArtistReleasesQueryResponse releases = releasesOpt.get();

        List<String> allTitles = new ArrayList<>(releases.getReleases().size());

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
                    .mbArtistId(mbParams.getMbArtistId())
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
