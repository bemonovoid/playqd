package com.bemonovoid.playqd.library.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.bemonovoid.playqd.data.dao.ArtistDao;
import com.bemonovoid.playqd.data.entity.AlbumEntity;
import com.bemonovoid.playqd.data.entity.ArtistEntity;
import com.bemonovoid.playqd.data.entity.SongEntity;
import com.bemonovoid.playqd.library.service.MusicDirectory;
import com.bemonovoid.playqd.library.service.MusicDirectoryScanner;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;

public class MusicDirectoryScannerImpl implements MusicDirectoryScanner {

    private static final Logger LOG = LoggerFactory.getLogger(MusicDirectoryScannerImpl.class);

    private final Set<String> audioExtensions = Set.of("flac", "mp3", "wav", "wma");
    private final Set<String> imageExtensions = Set.of("jpeg", "jpg");

    private final Map<String, ArtistEntity> artists = new ConcurrentHashMap<>();

    private final ArtistDao artistDao;
    private final MusicDirectory musicDirectory;

    public MusicDirectoryScannerImpl(ArtistDao artistDao, MusicDirectory musicDirectory) {
        this.artistDao = artistDao;
        this.musicDirectory = musicDirectory;
    }

    @Override
    @Async
    @CacheEvict(cacheNames = {"artists", "artist-albums", "album-songs"}, allEntries = true)
    public void scan() {
        try {
            Stream<Path> allPaths = Files.walk(musicDirectory.basePath(), 10);
            allPaths
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .filter(f -> {
                        String fileExtension = getFileExtension(f.getName());
                        return audioExtensions.contains(fileExtension.toLowerCase());
                    })
                    .forEach(this::refreshLibraryFile);
            artistDao.saveAll(artists.values());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            LOG.info("Scan completed");
        }
    }

    private void refreshLibraryFile(File file) {
        try {
            AudioFile audiofile = AudioFileIO.read(file);
            refreshLibraryFile(audiofile);
        } catch (Exception e) {
            LOG.error(String.format("Failed to refresh a file: %s", file.getAbsolutePath()), e);
        }
    }

    private void refreshLibraryFile(AudioFile audiofile) {
        SongEntity songEntity = new SongEntity();

        if (audiofile.getTag() != null) {
            Tag tag = audiofile.getTag();
            String songName = tag.getFirst(FieldKey.TITLE);
            if (songName == null || songName.isBlank()) {
                songName = songNameFromFileName(audiofile.getFile().getName());
            }
            songEntity.setName(songName);
            songEntity.setTrackId(tag.getFirst(FieldKey.TRACK));
            songEntity.setComment(tag.getFirst(FieldKey.COMMENT));
        } else {
            songEntity.setName(songNameFromFileName(audiofile.getFile().getName()));
        }

        AudioHeader audioHeader = audiofile.getAudioHeader();

        songEntity.setAudioEncodingType(audioHeader.getEncodingType());
        songEntity.setAudioChannelType(audioHeader.getChannels());
        songEntity.setAudioSampleRate(audioHeader.getSampleRate());
        songEntity.setAudioBitRate(audioHeader.getBitRate());
        songEntity.setDuration(audioHeader.getTrackLength());
        songEntity.setFileName(audiofile.getFile().getName());
        songEntity.setFileLocation(audiofile.getFile().getAbsolutePath());
        songEntity.setFileExtension(getFileExtension(audiofile.getFile().getName()));

        saveToAlbum(songEntity, audiofile);
    }

    private ArtistEntity getArtistEntity(AudioFile audioFile) {
        String name = getArtistName(audioFile);
        String nameAsKey = name.toLowerCase();
        if (artists.containsKey(nameAsKey)) {
            return artists.get(nameAsKey);
        } else {
            ArtistEntity artistEntity = new ArtistEntity();
            artistEntity.setName(name);
            artists.putIfAbsent(nameAsKey, artistEntity);
            return artistEntity;
        }
    }

    private void saveToAlbum(SongEntity songEntity, AudioFile audioFile) {
        ArtistEntity artistEntity = getArtistEntity(audioFile);
        String albumName = getAlbumName(audioFile);
        if (artistEntity.getAlbums() == null) {
            artistEntity.setAlbums(new ArrayList<>());
        }
        artistEntity.getAlbums().stream()
                .filter(a -> a.getName().equals(albumName))
                .findFirst()
                .ifPresentOrElse(
                        albumEntity -> albumEntity.addSong(songEntity),
                        () -> {
                            AlbumEntity albumEntity = new AlbumEntity();
                            albumEntity.setName(albumName);
                            if (audioFile.getTag() != null) {
                                albumEntity.setGenre(audioFile.getTag().getFirst(FieldKey.GENRE));
                                albumEntity.setDate(audioFile.getTag().getFirst(FieldKey.YEAR));
                            }
                            albumEntity.setArtLocation(getAlbumArtLocations(audioFile.getFile().getParentFile()));
                            artistEntity.addAlbum(albumEntity);
                            albumEntity.addSong(songEntity);
                        });
    }

    private List<String> getAlbumArtLocations(File albumFolder) {
        try {
            Stream<Path> albumDirPaths = Files.walk(albumFolder.toPath(), 3);
            return albumDirPaths
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .filter(f -> {
                        String fileExtension = getFileExtension(f.getName());
                        return imageExtensions.contains(fileExtension.toLowerCase());
                    })
                    .map(File::getAbsolutePath)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getArtistName(AudioFile audioFile) {
        String name = "Unknown artist";
        Tag tag = audioFile.getTag();
        if (tag != null) {
            name = tag.getFirst(FieldKey.ARTIST);
            if (name == null || name.isBlank()) {
                name = tag.getFirst(FieldKey.ALBUM_ARTIST);
            }
            if (name == null || name.isBlank()) {
                name = tag.getFirst(FieldKey.ORIGINAL_ARTIST);
            }
            if (name == null || name.isBlank()) {
                name = tag.getFirst(FieldKey.COMPOSER);
            }
            if (name == null || name.isBlank()) {
                name = "Unknown artist";
            }
        }
        return name;
    }

    private static String getAlbumName(AudioFile audioFile) {
        Tag tag = audioFile.getTag();
        if (tag != null) {
            String name = tag.getFirst(FieldKey.ALBUM);
            if (name != null && !name.isBlank()) {
                return name;
            }
        }
        return "Unknown album";
    }

    private static String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private static final String songNameFromFileName(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }
}
