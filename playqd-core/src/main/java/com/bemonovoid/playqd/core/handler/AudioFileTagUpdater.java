package com.bemonovoid.playqd.core.handler;

import java.io.File;
import java.util.List;

import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.Image;
import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.StandardArtwork;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public final class AudioFileTagUpdater {

    public static void updateArtistTags(Artist artist, List<String> audioFiles) {
        audioFiles.forEach(fileLocation -> updateFileAlbumTags(artist, null, fileLocation));
    }

    public static void updateAlbumTags(Album album, List<String> audioFiles) {
        audioFiles.forEach(fileLocation -> updateFileAlbumTags(null, album, fileLocation));
    }

    private static void updateFileAlbumTags(Artist artist, Album album, String fileLocation) {
        try {
            AudioFile audioFile = AudioFileIO.read(new File(fileLocation));
            Tag tag = audioFile.getTag();

            if (artist != null) {
                if (StringUtils.hasText(artist.getName())) {
                    tag.setField(FieldKey.ARTIST, artist.getName());
                }

                if (StringUtils.hasText(artist.getCountry())) {
                    tag.setField(FieldKey.COUNTRY, artist.getCountry());
                }

                if (StringUtils.hasText(artist.getSpotifyId())) {
                    tag.setField(FieldKey.MUSICBRAINZ_ARTISTID, artist.getSpotifyId());
                }
            }

            if (album != null) {
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
            }

            audioFile.commit();

            log.info("{} audio file successfully updated", fileLocation);
        } catch (Exception e) {
            log.error(String.format("Failed to update tags for audio file: %s", fileLocation), e);
        }
    }

    private static org.jaudiotagger.tag.images.Artwork buildArtworkTagFromResource(Image image) {
        org.jaudiotagger.tag.images.Artwork artwork = new StandardArtwork();
        artwork.setImageUrl(image.getUrl());
        artwork.setBinaryData(image.getData());
        artwork.setHeight(image.getDimensions().getHeight());
        artwork.setWidth(image.getDimensions().getWidth());
        return artwork;
    }
}
