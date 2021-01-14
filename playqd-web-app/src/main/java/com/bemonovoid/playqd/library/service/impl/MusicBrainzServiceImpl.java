package com.bemonovoid.playqd.library.service.impl;

import java.util.Objects;
import java.util.Optional;

import com.bemonovoid.playqd.library.model.musicbrainz.MBArtist;
import com.bemonovoid.playqd.library.model.musicbrainz.MBArtistQueryResponse;
import com.bemonovoid.playqd.library.model.musicbrainz.MBArtistReleasesQueryResponse;
import com.bemonovoid.playqd.library.model.musicbrainz.MBArtworkInfo;
import com.bemonovoid.playqd.library.model.musicbrainz.MBCoverArtQueryResponse;
import com.bemonovoid.playqd.library.model.musicbrainz.MBImage;
import com.bemonovoid.playqd.library.model.musicbrainz.MBRelease;
import com.bemonovoid.playqd.library.service.MusicBrainzApiClient;
import com.bemonovoid.playqd.library.service.MusicBrainzCoverArtApiClient;
import com.bemonovoid.playqd.library.service.MusicBrainzService;

public class MusicBrainzServiceImpl implements MusicBrainzService {

    private final MusicBrainzApiClient musicBrainzApiClient;
    private final MusicBrainzCoverArtApiClient coverArtApiClient;

    public MusicBrainzServiceImpl(MusicBrainzApiClient musicBrainzApiClient,
                                  MusicBrainzCoverArtApiClient coverArtApiClient) {
        this.musicBrainzApiClient = musicBrainzApiClient;
        this.coverArtApiClient = coverArtApiClient;
    }

    @Override
    public Optional<MBArtworkInfo> getArtworkInfo(String artistName, String albumName) {
        Optional<MBArtistQueryResponse> mayBeArtist = musicBrainzApiClient.getArtist(artistName);
        if (mayBeArtist.isEmpty()) {
            return Optional.empty();
        }

        MBArtistQueryResponse artist = mayBeArtist.get();

        if (artist.getCount() == 0 || artist.getArtists().isEmpty()) {
            return Optional.empty();
        }

        for (MBArtist mbArtist : artist.getArtists()) {
            Optional<MBArtworkInfo> artworkInfo = getArtworkInfoForMbArtist(mbArtist.getId(), albumName);
            if (artworkInfo.isEmpty()) {
                continue;
            }
            return artworkInfo;
        }

        return Optional.empty();
    }

    @Override
    public Optional<MBArtworkInfo> getArtworkInfoForMbArtist(String mbArtistId, String albumName) {

        Optional<MBArtistReleasesQueryResponse> mayBeReleases = musicBrainzApiClient.getReleases(mbArtistId);

        if (mayBeReleases.isEmpty()) {
            return Optional.empty();
        }

        String albumNameLookUp = albumName.toLowerCase();
        MBArtistReleasesQueryResponse releases = mayBeReleases.get();

        Optional<MBRelease> mayBeRelease = releases.getReleases().stream()
                .filter(release -> {
                    String releaseTitle = release.getTitle().toLowerCase();
                    return albumNameLookUp.equals(releaseTitle) || releaseTitle.startsWith(albumNameLookUp);
                })
                .findFirst();

        if (mayBeRelease.isEmpty()) {
            return Optional.empty();
        }

        MBRelease release = mayBeRelease.get();

        Optional<MBCoverArtQueryResponse> mayBeCoverArt = coverArtApiClient.getCoverArt(release.getId());

        if (mayBeCoverArt.isPresent()) {
            Optional<String> mayBeCoverArtUrl = getFront500pxCoverArtUrl(mayBeCoverArt.get());
            if (mayBeCoverArtUrl.isEmpty()) {
                mayBeCoverArtUrl = getDefaultFrontCoverArtUrl(mayBeCoverArt.get());
            }
            if (mayBeCoverArtUrl.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(new MBArtworkInfo(mbArtistId, release.getId(), mayBeCoverArtUrl.get()));
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
