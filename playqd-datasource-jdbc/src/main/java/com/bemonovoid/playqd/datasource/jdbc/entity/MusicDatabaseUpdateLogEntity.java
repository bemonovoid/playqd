package com.bemonovoid.playqd.datasource.jdbc.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.bemonovoid.playqd.core.model.DirectoryScanStatus;
import com.bemonovoid.playqd.datasource.jdbc.entity.system.PersistentAuditableEntity;
import lombok.Getter;
import lombok.Setter;

@Table(name = MusicDatabaseUpdateLogEntity.TABLE_NAME)
@Entity
@Getter
@Setter
public class MusicDatabaseUpdateLogEntity extends PersistentAuditableEntity {

    public static final String TABLE_NAME = "MUSIC_DATABASE_UPDATE_LOG";

    public static final String COL_STATUS = "STATUS";
    public static final String COL_DURATION = "DURATION";
    public static final String COL_DIRECTORY = "DIRECTORY";
    public static final String COL_ADDED_SONGS = "ADDED_SONGS";
    public static final String COL_CLEAN_ALL_APPLIED = "CLEAN_ALL_APPLIED";

    @Column(name = COL_DIRECTORY)
    private String directory;

    @Column(name = COL_CLEAN_ALL_APPLIED)
    private boolean cleanAllApplied;

    @Column(name = COL_ADDED_SONGS)
    private int numberOfSongsAdded;

    @Column(name = COL_STATUS)
    @Enumerated(EnumType.STRING)
    private DirectoryScanStatus status;

    @Column(name = COL_DURATION)
    private String duration;

}
