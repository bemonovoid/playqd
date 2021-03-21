package com.bemonovoid.playqd.datasource.jdbc.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.bemonovoid.playqd.datasource.jdbc.entity.system.PersistentAuditableEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = LibrarySettingsEntity.TABLE_NAME)
public class LibrarySettingsEntity extends PersistentAuditableEntity {

    static final String TABLE_NAME = "SETTINGS";

    private static final String COL_RESCAN_AT_STARTUP = "RESCAN_AT_STARTUP";
    private static final String COL_DELETE_BEFORE_SCAN = "DELETE_BEFORE_SCAN";

    @Column(name = COL_RESCAN_AT_STARTUP)
    private boolean rescanLibraryAtStartup;

    @Column(name = COL_DELETE_BEFORE_SCAN)
    private boolean deleteLibraryBeforeScan;

}
