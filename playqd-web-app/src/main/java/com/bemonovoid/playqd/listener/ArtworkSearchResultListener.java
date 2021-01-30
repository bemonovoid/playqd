package com.bemonovoid.playqd.listener;

import java.io.File;
import java.util.List;

import com.bemonovoid.playqd.core.dao.AlbumDao;
import com.bemonovoid.playqd.core.dao.ArtistDao;
import com.bemonovoid.playqd.core.dao.SongDao;
import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.ArtworkOnlineSearchResult;
import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.event.ArtworkResultReceived;
import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.StandardArtwork;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
class ArtworkSearchResultListener implements ApplicationListener<ArtworkResultReceived> {

    private final ArtistDao artistDao;
    private final AlbumDao albumDao;
    private final SongDao songDao;

    ArtworkSearchResultListener(ArtistDao artistDao, AlbumDao albumDao, SongDao songDao) {
        this.artistDao = artistDao;
        this.albumDao = albumDao;
        this.songDao = songDao;
    }

    @Override
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onApplicationEvent(ArtworkResultReceived event) {

        log.info("Handling {} event.", ArtworkSearchResultListener.class.getSimpleName());

        ArtworkOnlineSearchResult searchResult = event.getArtworkOnlineSearchResult();
        byte[] binaryData = new RestTemplate().getForObject(searchResult.getImageUrl(), byte[].class);

        Album album = event.getAlbum();

        albumDao.updateArtwork(album.getId(), searchResult.getMbReleaseId(), binaryData);

        artistDao.updateArtist(album.getArtist().getId(), searchResult.getMbArtistId(), searchResult.getMbArtistCountry());

        List<Song> albumSongs = songDao.getAlbumSongs(album.getId());

        Artwork artwork = createArtwork(searchResult.getImageUrl(), binaryData);

        for (Song song : albumSongs) {
            updateAudioFile(song.getFileLocation(), artwork);
        }
    }

    private Artwork createArtwork(String artworkUrl, byte[] binaryData) {
         Artwork artwork = new StandardArtwork();
         artwork.setImageUrl(artworkUrl);
         artwork.setBinaryData(binaryData);
         return artwork;
    }

    private void updateAudioFile(String fileLocation, Artwork artwork) {
        try {
            AudioFile audioFile = AudioFileIO.read(new File(fileLocation));
            Tag tag = audioFile.getTag();
            tag.setField(artwork);
            audioFile.commit();
            log.info("Artwork tag was successfully committed to audio file {}", fileLocation);
        } catch (Exception e) {
            log.error(String.format("Failed to commit artwork updates to audio file %s", fileLocation), e);
        }
    }

}
