package com.bemonovoid.playqd.core.handler;

import com.bemonovoid.playqd.core.dao.LibraryDao;
import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.MusicBrainzTagValues;
import com.bemonovoid.playqd.core.model.event.MusicBrainzTagsUpdateAvailable;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
class TagsUpdateAvailableEventHandler implements ApplicationListener<MusicBrainzTagsUpdateAvailable> {

    private final LibraryDao libraryDao;

    TagsUpdateAvailableEventHandler(LibraryDao libraryDao) {
        this.libraryDao = libraryDao;
    }

    @Async
    @Override
    public void onApplicationEvent(MusicBrainzTagsUpdateAvailable event) {
        MusicBrainzTagValues tagValues = event.getMusicBrainzTagValues();
        Artist artist = Artist.builder()
                .mbArtistId(tagValues.getMbArtistId())
                .country(tagValues.getMbArtistCountry())
                .build();
        libraryDao.ofArtist().updateArtist(artist);

        Album album = Album.builder().mbReleaseId(tagValues.getMbReleaseId()).build();
        libraryDao.ofAlbum().updateAlbum(album);
    }
}
