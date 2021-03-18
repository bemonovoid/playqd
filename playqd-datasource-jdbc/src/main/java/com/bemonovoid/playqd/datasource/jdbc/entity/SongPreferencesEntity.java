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
@Table(name = SongPreferencesEntity.TABLE_NAME)
public class SongPreferencesEntity extends PersistentAuditableEntity {

    public static final String TABLE_NAME = "SONG_PREFERENCES";

    private static final String COL_SONG_NAME_AS_FILE_NAME = "SONG_NAME_AS_FILE_NAME";

    @Column(name = COL_SONG_NAME_AS_FILE_NAME)
    private boolean songNameAsFileName;

    @ManyToOne(fetch = FetchType.LAZY)
    private SongEntity song;

}
