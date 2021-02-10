package com.bemonovoid.playqd.datasource.jdbc.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Table(name = FavoriteSongEntity.TABLE_NAME)
@Entity
@Getter
@Setter
public class FavoriteSongEntity extends PersistentAuditableEntity<Long> {

    public static final String TABLE_NAME = "FAVORITE_SONG";
    private static final String COL_SONG_ID = "SONG_ID";

    @Column(name = COL_SONG_ID)
    private long songId;
}
