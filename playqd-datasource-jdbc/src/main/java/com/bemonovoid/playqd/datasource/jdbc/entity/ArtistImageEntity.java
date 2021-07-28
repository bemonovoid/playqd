package com.bemonovoid.playqd.datasource.jdbc.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.bemonovoid.playqd.datasource.jdbc.entity.system.PersistentAuditableEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Table(name = ArtistImageEntity.TABLE_NAME)
@Entity
@Getter
@Setter
public class ArtistImageEntity extends PersistentAuditableEntity {

    public static final String TABLE_NAME = "ARTIST_IMAGE";
    public static final String COL_IMAGE = "IMAGE";
    public static final String COL_URL = "URL";
    public static final String COL_HEIGHT = "HEIGHT";
    public static final String COL_WIDTH = "WIDTH";
    public static final String COL_FORMAT = "FORMAT";

    @Column(name = COL_URL)
    private String url;

    @Column(name = COL_HEIGHT)
    private int height;

    @Column(name = COL_WIDTH)
    private int width;

    @Column(name = COL_FORMAT)
    private String format;

    @Lob
    @Column(name = COL_IMAGE)
    @Type(type="org.hibernate.type.ImageType")
    private byte[] image;

    @ManyToOne(fetch = FetchType.LAZY)
    private ArtistEntity artist;
}
