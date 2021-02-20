package com.bemonovoid.playqd.core.handler;

import java.io.File;

import com.bemonovoid.playqd.core.dao.SongDao;
import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.Image;
import com.bemonovoid.playqd.core.model.event.AlbumTagsUpdated;
import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.StandardArtwork;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
class AlbumTagsUpdatedHandler implements ApplicationListener<AlbumTagsUpdated> {

    private final SongDao songDao;

    AlbumTagsUpdatedHandler(SongDao songDao) {
        this.songDao = songDao;
    }

    @Override
    public void onApplicationEvent(AlbumTagsUpdated event) {
        songDao.getAlbumSongsFileLocations(event.getAlbum().getId())
                .forEach(fileLocation -> updateFile(fileLocation, event.getAlbum()));
    }

    private void updateFile(String fileLocation, Album album) {
        try {
            AudioFile audioFile = AudioFileIO.read(new File(fileLocation));
            Tag tag = audioFile.getTag();

            if (StringUtils.hasText(album.getName())) {
                tag.setField(FieldKey.ALBUM, album.getName());
            }

            if (StringUtils.hasText(album.getDate())) {
                tag.setField(FieldKey.YEAR, album.getDate());
            }

            if (StringUtils.hasText(album.getGenre())) {
                tag.setField(FieldKey.GENRE, album.getGenre());
            }

            if (album.getImage() != null && album.getImage().getData() != null) {
                tag.setField(buildArtworkTagFromResource(album.getImage()));
            }

            if (StringUtils.hasText(album.getMbReleaseId())) {
                tag.setField(FieldKey.MUSICBRAINZ_RELEASEID, album.getMbReleaseId());
            }

            audioFile.commit();
        } catch (Exception e) {
            log.error(String.format("Failed to update tags for audio file: %s", fileLocation), e);
        }
    }

    private org.jaudiotagger.tag.images.Artwork buildArtworkTagFromResource(Image albumImage) {
        org.jaudiotagger.tag.images.Artwork artwork = new StandardArtwork();
        artwork.setImageUrl(albumImage.getUrl());
        artwork.setBinaryData(albumImage.getData());
        return artwork;
    }
}
