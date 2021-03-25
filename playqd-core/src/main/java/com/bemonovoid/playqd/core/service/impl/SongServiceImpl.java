package com.bemonovoid.playqd.core.service.impl;

import java.util.List;

import com.bemonovoid.playqd.core.dao.SongDao;
import com.bemonovoid.playqd.core.handler.AudioFileTagUpdater;
import com.bemonovoid.playqd.core.model.Song;
import com.bemonovoid.playqd.core.model.SortDirection;
import com.bemonovoid.playqd.core.model.UpdateOptions;
import com.bemonovoid.playqd.core.model.pageable.FindSongsRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableRequest;
import com.bemonovoid.playqd.core.model.pageable.PageableResult;
import com.bemonovoid.playqd.core.model.pageable.SortBy;
import com.bemonovoid.playqd.core.model.pageable.SortRequest;
import com.bemonovoid.playqd.core.service.SongService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
class SongServiceImpl implements SongService {

    private final SongDao songDao;

    SongServiceImpl(SongDao songDao) {
        this.songDao = songDao;
    }

    @Override
    public Song getSong(String songId) {
        return songDao.getOne(songId);
    }

    @Override
    public String getSongFileLocation(String songId) {
        return songDao.getSongFileLocation(songId);
    }

    @Override
    public List<String> getAlbumSongsFormats(String albumId) {
        return songDao.getAvailableFormats(albumId);
    }

    @Override
    public PageableResult<Song> getSongs(FindSongsRequest request) {
        SortBy sortBy = SortBy.NAME;
        SortDirection direction = SortDirection.ASC;
        if (request.getSortBy() != null) {
            sortBy = SortBy.valueOf(request.getSortBy().name());
            direction = request.getDirection();
        }
        SortRequest sort = SortRequest.builder().sortBy(sortBy).direction(direction).build();
        PageableRequest pageableRequest = new PageableRequest(request.getPage(), request.getSize(), sort);

        if (StringUtils.hasText(request.getName())) {
            return songDao.getSongsWithNameContaining(request.getName(), pageableRequest);
        }
        if (SortBy.RECENTLY_ADDED == sortBy) {
            return songDao.getRecentlyAddedSongs(pageableRequest);
        } else if (SortBy.RECENTLY_PLAYED == sortBy) {
            return songDao.getRecentlyPlayedSongs(pageableRequest);
        } else if (SortBy.MOST_PLAYED == sortBy) {
            return songDao.getMostPlayedSongs(pageableRequest);
        } else if (SortBy.FAVORITES == sortBy) {
            return songDao.getFavoriteSongs(pageableRequest);
        } else {
            return songDao.getSongs(pageableRequest);
        }
    }

    @Override
    public PageableResult<Song> getAlbumSongs(String albumId, FindSongsRequest request) {
        int pageSize = Integer.MAX_VALUE;
        if (request.getSize() > 0) {
            pageSize = request.getSize();
        }
        SortRequest sort = SortRequest.builder()
                .sortBy(SortBy.valueOf(request.getSortBy().name())).direction(request.getDirection()).build();
        PageableRequest pageableRequest = new PageableRequest(request.getPage(), pageSize, sort);
        String audioFormat = request.getFormat();
        return songDao.getAlbumSongs(albumId, audioFormat, pageableRequest);
    }

    @Override
    public PageableResult<Song> getArtistSongs(String artistId, FindSongsRequest request) {
        return songDao.getArtistSongs(artistId, new PageableRequest(request.getPage(), request.getSize(), null));
    }

    @Override
    public void updateSong(Song song, UpdateOptions options) {
        songDao.updateSong(song);
        if (options.isUpdateAudioTags()) {
            AudioFileTagUpdater.updateSongTags(song, songDao.getSongFileLocation(song.getId()));
        }
    }

    @Override
    public void updateFavoriteFlag(String songId, boolean isFavorite) {
        songDao.updateFavoriteFlag(songId, isFavorite);
    }

    @Override
    public void moveSong(String songId, String albumIdTo, UpdateOptions updateOptions) {
        Song song = songDao.moveSong(songId, albumIdTo);
        if (updateOptions.isUpdateAudioTags()) {
            AudioFileTagUpdater.updateAlbumTags(song.getAlbum(), List.of(songDao.getSongFileLocation(songId)));
        }
    }

    @Override
    public void updatePlayCount(String songId) {
        songDao.updatePlayCount(songId);
    }

}
