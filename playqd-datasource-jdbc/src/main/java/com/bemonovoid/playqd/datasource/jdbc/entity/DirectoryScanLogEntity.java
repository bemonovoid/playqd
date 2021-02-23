package com.bemonovoid.playqd.datasource.jdbc.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.bemonovoid.playqd.core.model.DirectoryScanStatus;
import lombok.Getter;
import lombok.Setter;

@Table(name = DirectoryScanLogEntity.TABLE_NAME)
@Entity
@Getter
@Setter
public class DirectoryScanLogEntity extends PersistentAuditableEntity<Long> {

    public static final String TABLE_NAME = "DIRECTORY_SCAN_LOG";

    public static final String COL_DIRECTORY = "DIRECTORY";
    public static final String COL_CLEAN_ALL_APPLIED = "CLEAN_ALL_APPLIED";
    public static final String COL_ADDED_SONGS = "ADDED_SONGS";
    public static final String COL_STATUS = "STATUS";
    public static final String COL_DURATION = "DURATION";

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
    private long duration;

}
