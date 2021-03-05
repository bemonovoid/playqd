package com.bemonovoid.playqd.datasource.jdbc.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.bemonovoid.playqd.datasource.jdbc.entity.system.PersistentAuditableEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = PlaybackInfoEntity.TABLE_NAME)
public class PlaybackInfoEntity extends PersistentAuditableEntity<Long> {

    public static final String TABLE_NAME = "PLAYBACK_INFO";

    private static final String COL_PLAY_COUNT = "PLAY_COUNT";
    private static final String COL_FAVORITE = "FAVORITE";

    @Column(name = COL_PLAY_COUNT)
    private int playCount;

    @Column(name = COL_FAVORITE)
    private boolean favorite;

    @ManyToOne(fetch = FetchType.LAZY)
    private SongEntity song;

}
