package com.bemonovoid.playqd.core.handler;

import java.io.File;

import com.bemonovoid.playqd.core.dao.SongDao;
import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.event.ArtistTagsUpdated;
import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
class ArtistTagsUpdatedHandler implements ApplicationListener<ArtistTagsUpdated> {

    private final SongDao songDao;

    public ArtistTagsUpdatedHandler(SongDao songDao) {
        this.songDao = songDao;
    }

    @Override
    public void onApplicationEvent(ArtistTagsUpdated event) {
        songDao.getArtistSongsFileLocations(event.getArtist().getId())
                .forEach(fileLocation -> updateFile(fileLocation, event.getArtist()));
    }

    private void updateFile(String fileLocation, Artist artist) {
        try {
            AudioFile audioFile = AudioFileIO.read(new File(fileLocation));
            Tag tag = audioFile.getTag();

            if (StringUtils.hasText(artist.getName())) {
                tag.setField(FieldKey.ARTIST, artist.getName());
            }

            if (StringUtils.hasText(artist.getCountry())) {
                tag.setField(FieldKey.COUNTRY, artist.getCountry());
            }

            audioFile.commit();
        } catch (Exception e) {
            log.error(String.format("Failed to update tags for audio file: %s", fileLocation), e);
        }
    }
}
