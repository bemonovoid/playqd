package com.bemonovoid.playqd.core.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class ImageServiceImpl implements ImageService {

    private final SongDao songDao;
    private final WorkingDir workingDir;
    private final ImageSearchService imageSearchService;
    private final BinaryResourceReader binaryResourceReader;

    ImageServiceImpl(SongDao songDao,
                     WorkingDir workingDir,
                     ImageSearchService imageSearchService,
                     BinaryResourceReader binaryResourceReader) {
        this.songDao = songDao;
        this.workingDir = workingDir;
        this.imageSearchService = imageSearchService;
        this.binaryResourceReader = binaryResourceReader;
    }

//    @Override
//    public Artwork getAlbumArtworkFromLibrary(ArtworkLocalSearchQuery query) {
//        if (query.getAlbumId() != null) {
//            Optional<Album> albumOpt = libraryDao.ofAlbum().findOne(query.getAlbumId());
//            if (albumOpt.isEmpty()) {
//                return getDefault();
//            }
//
//            Album album = albumOpt.get();
//            if (album.getArtwork() != null && album.getArtwork().getBinary() != null) {
//                return album.getArtwork();
//            } else {
//                Song albumSong = getSong(query).get();
//                return getArtworkFromAudioFile(albumSong.getFileLocation());
//            }
//        }
//        return getDefault();
//    }

//    @Override
//    public Optional<String> getAlbumArtworkOnline(ArtworkLocalSearchQuery localQuery) {
//        Optional<Song> songOpt = getSong(localQuery);
//
//        if (songOpt.isEmpty()){
//            return Optional.empty();
//        }
//
//        Song song = songOpt.get();
//        Album album = song.getAlbum();
//
//        ArtworkOnlineSearchQuery onlineSearchQuery = ArtworkOnlineSearchQuery.builder()
//                .artistName(song.getArtist().getSimpleName())
//                .mbArtistId(song.getArtist().getMbArtistId())
//                .albumName(album.getSimpleName())
//                .build();
//
//        Optional<ArtworkOnlineSearchResult> artworkSearchResultOpt = imageSearchService.search(onlineSearchQuery);
//
//        if (artworkSearchResultOpt.isEmpty()) {
//            return Optional.empty();
//        }
//
//        ArtworkOnlineSearchResult searchResult = artworkSearchResultOpt.get();
//
//        if (StringUtils.hasText(searchResult.getMbArtistId())) {
//            eventPublisher.publishEvent(new ArtistTagsUpdated(this, Artist.builder()
//                    .id(album.getArtist().getId())
//                    .mbArtistId(searchResult.getMbArtistId())
//                    .country(searchResult.getMbArtistCountry())
//                    .build()));
//        }
//
//        if (StringUtils.hasText(searchResult.getMbReleaseId())) {
//            eventPublisher.publishEvent(new AlbumTagsUpdated(this, Album.builder()
//                    .id(album.getId()).mbReleaseId(searchResult.getMbReleaseId()).build()));
//        }
//
//        updateAlbumArtwork(album.getId(), searchResult.getImageUrl());
//
//        return Optional.of(searchResult.getImageUrl());
//    }

//    @Override
//    public void updateAlbumArtwork(long albumId, String src) {
//        Artwork artwork = Artwork.builder().src(src).binary(binaryResourceReader.read(src)).build();
//        eventPublisher.publishEvent(new AlbumTagsUpdated(this, Album.builder().artwork(artwork).build()));
//        libraryDao.ofAlbum().setArtworkBinary(albumId, artwork.getBinary());
//    }

    @Override
    public Optional<Image> getAlbumImage(Album album, boolean findRemotely) {
        return songDao.getFirstSongInAlbum(album.getId()).map(song -> getArtworkFromAudioFile(song.getFileLocation()));
    }

    @Override
    public Optional<Image> getArtistImage(Artist artist, ImageSize size, boolean findRemotely) {

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
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }  else if (findRemotely) {
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
            return getDefault();
        } catch (Exception e) {
            log.error("Failed to retrieve an audio file for path {}. Default artwork will be used", audioFileLocation);
            return getDefault();
        }
    }

    private static Image getDefault() {
        ClassPathResource classPathResource = new ClassPathResource("/public/images/default-album-cover.png");
        try (InputStream is = classPathResource.getInputStream()) {
            return new Image(classPathResource.getPath(), is.readAllBytes(), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
