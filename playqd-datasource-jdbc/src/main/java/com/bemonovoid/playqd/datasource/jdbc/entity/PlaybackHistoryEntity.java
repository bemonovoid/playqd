package com.bemonovoid.playqd.datasource.jdbc.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Table(name = PlaybackHistoryEntity.TABLE_NAME)
@Entity
@Getter
@Setter
public class PlaybackHistoryEntity extends PersistentAuditableEntity<Long> {

    public static final String TABLE_NAME = "PLAYBACK_HISTORY";

    @ManyToOne(fetch = FetchType.LAZY)
    private SongEntity song;

}
