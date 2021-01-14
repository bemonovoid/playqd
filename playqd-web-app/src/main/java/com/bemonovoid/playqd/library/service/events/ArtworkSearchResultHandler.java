package com.bemonovoid.playqd.library.service.events;

import java.io.File;
import java.util.List;

import com.bemonovoid.playqd.data.dao.AlbumDao;
import com.bemonovoid.playqd.data.dao.ArtistDao;
import com.bemonovoid.playqd.data.dao.SongDao;
import com.bemonovoid.playqd.data.entity.AlbumEntity;
import com.bemonovoid.playqd.data.entity.ArtistEntity;
import com.bemonovoid.playqd.data.entity.ArtworkStatus;
import com.bemonovoid.playqd.data.entity.SongEntity;
import com.bemonovoid.playqd.online.search.ArtworkSearchResult;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.StandardArtwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Component
public class ArtworkSearchResultHandler implements ApplicationListener<ArtworkResultReceived> {

    private static final Logger LOG = LoggerFactory.getLogger(ArtworkSearchResultHandler.class);

    private final ArtistDao artistDao;
    private final AlbumDao albumDao;
    private final SongDao songDao;

    public ArtworkSearchResultHandler(ArtistDao artistDao, AlbumDao albumDao, SongDao songDao) {
        this.artistDao = artistDao;
        this.albumDao = albumDao;
        this.songDao = songDao;
    }

    @Override
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onApplicationEvent(ArtworkResultReceived event) {

        LOG.info("Handling {} event.", ArtworkSearchResultHandler.class.getSimpleName());

        ArtworkSearchResult searchResult = event.getArtworkSearchResult();

        Long albumId = event.getAlbumId();

        AlbumEntity albumEntity = albumDao.getOne(event.getAlbumId()).get();
        albumEntity.setMbReleaseId(searchResult.getMbReleaseId());
        albumEntity.setArtworkStatus(ArtworkStatus.AVAILABLE);
        albumDao.save(albumEntity);

        ArtistEntity artistEntity = artistDao.getOne(albumEntity.getArtist().getId());
        artistEntity.setMbArtistId(searchResult.getMbArtistId());
        artistDao.save(artistEntity);

        List<SongEntity> albumSongs = songDao.getAlbumSongs(albumId);

        Artwork artwork = createArtwork(searchResult.getImageUrl());

        for (SongEntity songEntity : albumSongs) {
            updateAudioFile(songEntity.getFileLocation(), artwork);
        }
    }

    private Artwork createArtwork(String artworkUrl) {
         byte[] binaryData = new RestTemplate().getForObject(artworkUrl, byte[].class);
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
            LOG.info("Artwork tag for file {} was successfully set", fileLocation);
        } catch (Exception e) {
            LOG.error(String.format("Failed to set Artwork tag for audio file %s", fileLocation), e);
        }
    }

}
