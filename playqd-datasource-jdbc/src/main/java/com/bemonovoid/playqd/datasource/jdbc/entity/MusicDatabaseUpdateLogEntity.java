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

    public static final String COL_SCAN_STATUS = "SCAN_STATUS";
    public static final String COL_SCAN_DURATION_IN_MILLIS = "SCAN_DURATION_IN_MILLIS";
    public static final String COL_SCAN_DIRECTORY = "SCAN_DIRECTORY";
    public static final String COL_FILES_INDEXED = "FILES_INDEXED";
    public static final String COL_INDEXED_FILES_MISSING = "INDEXED_FILES_MISSING";
    public static final String COL_DELETE_ALL_BEFORE_SCAN = "DELETE_ALL_BEFORE_SCAN";
    public static final String COL_SCAN_STATUS_DETAILS = "SCAN_STATUS_DETAILS";

    @Column(name = COL_SCAN_DIRECTORY)
    private String scanDirectory;

    @Column(name = COL_DELETE_ALL_BEFORE_SCAN)
    private boolean deleteAllBeforeScan;

    @Column(name = COL_FILES_INDEXED)
    private int filesIndexed;

    @Column(name = COL_INDEXED_FILES_MISSING)
    private int indexedFilesMissing;

    @Column(name = COL_SCAN_STATUS)
    @Enumerated(EnumType.STRING)
    private DirectoryScanStatus scanStatus;

    @Column(name = COL_SCAN_STATUS_DETAILS)
    private String statusDetails;

    @Column(name = COL_SCAN_DURATION_IN_MILLIS)
    private long scanDurationInMillis;

}
