package com.bemonovoid.playqd.core.handler;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.Image;
import com.bemonovoid.playqd.core.model.Song;
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
        audioFiles.forEach(fileLocation -> {
            updateAudioFileTags(fileLocation, tag -> {
                try {
                    if (StringUtils.hasText(artist.getName())) {
                        tag.setField(FieldKey.ARTIST, artist.getName());
                    }
                    if (StringUtils.hasText(artist.getCountry())) {
                        tag.setField(FieldKey.COUNTRY, artist.getCountry());
                    }
                } catch (Exception e) {
                    log.error(String.format("Failed to update artist tags for audio file: %s", fileLocation), e);
                }
            });
        });
    }

    public static void updateAlbumTags(Album album, List<String> audioFiles) {
        audioFiles.forEach(fileLocation -> {
            updateAudioFileTags(fileLocation, tag -> {
                try {
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
                } catch (Exception e) {
                    log.error(String.format("Failed to update album tags for audio file: %s", fileLocation), e);
                }
            });
        });
    }

    public static void updateSongTags(Song song, String fileLocation) {
        updateAudioFileTags(fileLocation, tag -> {
            try {
                if (StringUtils.hasText(song.getName())) {
                    tag.setField(FieldKey.TITLE, song.getName());
                }
                if (StringUtils.hasText(song.getComment())){
                    tag.setField(FieldKey.COMMENT, song.getComment());
                }
                if (StringUtils.hasText(song.getLyrics())){
                    tag.setField(FieldKey.LYRICS, song.getLyrics());
                }
                if (StringUtils.hasText(song.getTrackId())){
                    tag.setField(FieldKey.TRACK, song.getTrackId());
                }
            } catch (Exception e) {
                log.error(String.format("Failed to update song tags for audio file: %s", fileLocation), e);
            }
        });
    }

    private static void updateAudioFileTags(String fileLocation, Consumer<Tag> tagConsumer) {
        try {
            AudioFile audioFile = AudioFileIO.read(new File(fileLocation));

            Tag tag = audioFile.getTag();

            tagConsumer.accept(tag);

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
