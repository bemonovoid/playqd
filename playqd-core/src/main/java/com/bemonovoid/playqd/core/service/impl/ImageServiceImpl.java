package com.bemonovoid.playqd.core.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.bemonovoid.playqd.core.dao.AlbumDao;
import com.bemonovoid.playqd.core.dao.SongDao;
import com.bemonovoid.playqd.core.model.Album;
import com.bemonovoid.playqd.core.model.Artist;
import com.bemonovoid.playqd.core.model.Dimensions;
import com.bemonovoid.playqd.core.model.Image;
import com.bemonovoid.playqd.core.model.ImageSize;
import com.bemonovoid.playqd.core.model.WorkingDir;
import com.bemonovoid.playqd.core.service.BinaryResourceReader;
import com.bemonovoid.playqd.core.service.ImageSearchService;
import com.bemonovoid.playqd.core.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.StandardArtwork;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class ImageServiceImpl implements ImageService {

    private final SongDao songDao;
    private final AlbumDao albumDao;
    private final WorkingDir workingDir;
    private final ImageSearchService imageSearchService;
    private final BinaryResourceReader binaryResourceReader;

    ImageServiceImpl(SongDao songDao,
                     AlbumDao albumDao,
                     WorkingDir workingDir,
                     ImageSearchService imageSearchService,
                     BinaryResourceReader binaryResourceReader) {
        this.songDao = songDao;
        this.albumDao = albumDao;
        this.workingDir = workingDir;
        this.imageSearchService = imageSearchService;
        this.binaryResourceReader = binaryResourceReader;
    }

    @Override
    public Optional<Image> getAlbumImage(Album album, ImageSize size, boolean findRemotely) {
        Optional<String> fileLocationOpt = songDao.getAnyAlbumSongFileLocation(album.getId());
        if (fileLocationOpt.isPresent()) {
            Image image = getArtworkFromAudioFile(fileLocationOpt.get());
            if (image != null) {
                return Optional.of(image);
            }
            if (findRemotely) {
                List<Image> images = imageSearchService.searchAlbumImage(album);
                if (!images.isEmpty()) {
                    Image imageFromRemote = images.get(0);
                    saveAlbumImage(album, imageFromRemote);
                    return Optional.of(imageFromRemote);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Image> getArtistImage(Artist artist, ImageSize size, boolean findRemotely) {
        try {
            Path artistImagesPath = Paths.get(workingDir.getPath().toString(), "artists", artist.getName());

            if (Files.exists(artistImagesPath)) {
                try (Stream<Path> files = Files.list(artistImagesPath)) {
                    List<Path> sortedFiles = files.sorted(new ImageSizeComparator()).collect(Collectors.toList());
                    byte[] data;
                    if (ImageSize.SMALL == size) {
                        data = Files.readAllBytes(sortedFiles.get(0));
                    } else {
                        data = Files.readAllBytes(sortedFiles.get(sortedFiles.size() - 1));
                    }
                    return Optional.of(new Image(artistImagesPath.toString(), data, null));
                }
            } else if (findRemotely) {
                List<Image> images = imageSearchService.searchArtistImage(artist);
                if (!images.isEmpty()) {
                    List<Image> sortedImages = images.stream()
                            .peek(artistImage -> saveArtistImage(artist, artistImage))
                            .sorted(Comparator.comparingInt(i -> i.getDimensions().getHeight()))
                            .collect(Collectors.toList());
                    if (ImageSize.SMALL == size) {
                        return Optional.of(sortedImages.get(0));
                    } else {
                        return Optional.of(sortedImages.get(sortedImages.size() - 1));
                    }
                }
                return Optional.empty();
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            log.error("Failed to read from file: " + e.getMessage());
            return Optional.empty();
        } catch (InvalidPathException e) {
            log.error("Failed to resolve audio file path: " + e.getInput());
            return Optional.empty();
        }
    }

    private void saveArtistImage(Artist artist, Image image) {
        try {
            Dimensions size = image.getDimensions();
            String fileName = String.format("%sx%s.jpg", size.getHeight(), size.getWidth());
            byte[] data = binaryResourceReader.read(image.getUrl());
            Path filePath = Paths.get(workingDir.getPath().toString(), "artists", artist.getName(), fileName);
            if (!Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }
            Files.write(filePath, data);
        } catch (IOException e) {
            log.error("Failed to write file to working dir", e);
        }
    }

    private void saveAlbumImage(Album album, Image image) {
        Artwork artwork = buildArtworkTagFromImage(image);
        try {
            for (String fileLocation : songDao.getAlbumSongsFileLocations(album.getId())) {
                updateFile(fileLocation, artwork);
            }
        } catch (Exception e) {
            log.error(String.format("Failed to update tags for audio file(s) in album: %s", album.getId()), e);
            albumDao.saveAlbumImage(album.getId(), image.getData());
        }
    }

    private void updateFile(String fileLocation, Artwork artwork) throws Exception {
        AudioFile audioFile = AudioFileIO.read(new File(fileLocation));
        Tag tag = audioFile.getTag();
        if (tag != null) {
            tag.setField(artwork);
            audioFile.commit();
        } else {
            log.warn("%s audio file tag is null. Artwork tag won't be saved");
        }
    }

    private org.jaudiotagger.tag.images.Artwork buildArtworkTagFromImage(Image image) {
        org.jaudiotagger.tag.images.Artwork artwork = new StandardArtwork();
        artwork.setImageUrl(image.getUrl());
        artwork.setBinaryData(image.getData());
        artwork.setHeight(image.getDimensions().getHeight());
        artwork.setWidth(image.getDimensions().getWidth());
        return artwork;
    }

    private Image getArtworkFromAudioFile(String audioFileLocation) {
        File file = new File(audioFileLocation);
        try {
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();
            if (tag != null) {
                org.jaudiotagger.tag.images.Artwork artwork = tag.getFirstArtwork();
                if (artwork != null) {
                    Dimensions size = new Dimensions(artwork.getHeight(), artwork.getWidth());
                    return new Image(artwork.getImageUrl(), artwork.getBinaryData(), size);
                }
            }
            return null;
        } catch (Exception e) {
            log.error("Failed to retrieve an audio file for path {}. Default artwork will be used", audioFileLocation);
            return null;
        }
    }

    private static class ImageSizeComparator implements Comparator<Path> {

        @Override
        public int compare(Path p1, Path p2) {
            String fName1 = p1.toFile().getName();
            String fName2 = p2.toFile().getName();
            int size1 = Integer.parseInt(fName1.substring(0, fName1.indexOf('x')));
            int size2 = Integer.parseInt(fName2.substring(0, fName2.indexOf('x')));
            return Integer.compare(size1, size2);
        }
    }
}
