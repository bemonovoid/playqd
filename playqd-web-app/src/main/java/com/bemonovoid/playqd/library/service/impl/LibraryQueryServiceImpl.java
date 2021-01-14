package com.bemonovoid.playqd.library.service.impl;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.bemonovoid.playqd.data.dao.AlbumDao;
import com.bemonovoid.playqd.data.dao.ArtistDao;
import com.bemonovoid.playqd.data.dao.SongDao;
import com.bemonovoid.playqd.data.entity.AlbumEntity;
import com.bemonovoid.playqd.data.entity.ArtistEntity;
import com.bemonovoid.playqd.data.entity.SongEntity;
import com.bemonovoid.playqd.library.model.Album;
import com.bemonovoid.playqd.library.model.AlbumHelper;
import com.bemonovoid.playqd.library.model.AlbumSongs;
import com.bemonovoid.playqd.library.model.Artist;
import com.bemonovoid.playqd.library.model.ArtistAlbums;
import com.bemonovoid.playqd.library.model.Artists;
import com.bemonovoid.playqd.library.model.Song;
import com.bemonovoid.playqd.library.model.SongHelper;
import com.bemonovoid.playqd.library.model.query.AlbumSongsQuery;
import com.bemonovoid.playqd.library.model.query.ArtistAlbumsQuery;
import com.bemonovoid.playqd.library.model.query.SongQuery;
import com.bemonovoid.playqd.library.service.LibraryQueryService;
import com.bemonovoid.playqd.online.search.ArtworkBinary;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class LibraryQueryServiceImpl implements LibraryQueryService {

    private static final Logger LOG = LoggerFactory.getLogger(LibraryQueryServiceImpl.class);

    private final ArtistDao artistDao;
    private final AlbumDao albumDao;
    private final SongDao songDao;

    public LibraryQueryServiceImpl(ArtistDao artistDao, AlbumDao albumDao, SongDao songDao) {
        this.artistDao = artistDao;
        this.albumDao = albumDao;
        this.songDao = songDao;
    }

    @Override
    public Artists getArtists() {
        return new Artists(artistDao.getAll().stream()
                .map(artistEntity -> new Artist(artistEntity.getId(), artistEntity.getName()))
                .sorted(Comparator.comparing(Artist::getName))
                .collect(Collectors.toList()));
    }

    @Override
    public Album getAlbum(long albumId) {
        return AlbumHelper.fromEntity(albumDao.getOne(albumId).get());
    }

    @Override
    public ArtistAlbums getArtistAlbums(ArtistAlbumsQuery query) {
        List<AlbumEntity> albumEntities = albumDao.getArtistAlbums(query.getArtistId());
        ArtistEntity artistEntity = albumEntities.get(0).getArtist();
        List<Album> albums = albumEntities.stream().map(AlbumHelper::fromEntity).collect(Collectors.toList());
        return new ArtistAlbums(new Artist(artistEntity.getId(), artistEntity.getName()), albums);
    }

    @Override
    public Optional<ArtworkBinary> getArtworkBySongId(long songId) {
        return songDao.getOne(songId).map(this::getArtwork);
    }

    @Override
    public Optional<ArtworkBinary> getArtworkByAlbumId(long albumId) {
        return songDao.getFirstSongInAlbum(albumId).map(this::getArtwork);
    }

    @Override
    public Optional<Song> getSong(SongQuery query) {
        return songDao.getOne(query.getSongId()).map(songEntity -> {
            Song song = SongHelper.fromEntity(songEntity);
            song.setArtist(new Artist(songEntity.getArtist().getId(), songEntity.getArtist().getName()));
            song.setAlbum(AlbumHelper.fromEntity(songEntity.getAlbum()));
            return song;
        });
    }

    @Override
    public AlbumSongs getAlbumSongs(AlbumSongsQuery query) {
        AlbumEntity albumEntity = albumDao.getOne(query.getAlbumId()).get();
        Album album = AlbumHelper.fromEntity(albumEntity);
        Artist artist = album.getArtist();
        List<Song> songs = albumEntity.getSongs().stream()
                .map(songEntity -> {
                    Song song = SongHelper.fromEntity(songEntity);
                    song.setArtist(artist);
                    song.setAlbum(album);
                    return song;
                })
                .collect(Collectors.toList());
        return new AlbumSongs(artist, album, songs);
    }

    private ArtworkBinary getArtwork(SongEntity songEntity) {
        File file = new File(songEntity.getFileLocation());
        try {
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();
            if (tag != null) {
                Artwork artwork = tag.getFirstArtwork();
                if (artwork != null) {
                    return new ArtworkBinary(artwork.getBinaryData(), artwork.getMimeType());
                }
            }
            return ArtworkBinary.empty();
        } catch (Exception e) {
            LOG.error("Failed to retrieve an audio file for path {}", songEntity.getFileLocation());
            return ArtworkBinary.empty();
        }
    }
}
