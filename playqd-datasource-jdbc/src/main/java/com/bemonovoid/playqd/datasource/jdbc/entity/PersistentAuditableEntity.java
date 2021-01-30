package com.bemonovoid.playqd.datasource.jdbc.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class PersistentAuditableEntity<ID extends Serializable> implements Persistable<ID> {

    public static final String COL_PK_ID = "ID";

    public static final String COL_CREATED_BY = "CREATED_BY";
    public static final String COL_CREATED_DATE = "CREATED_DATE";
    public static final String COL_LAST_MODIFIED_BY = "LAST_MODIFIED_BY";
    public static final String COL_LAST_MODIFIED_DATE = "LAST_MODIFIED_DATE";

    @Id
    @Column(name = COL_PK_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ID id;

    @Override
    public boolean isNew() {
        return getId() != null;
    }

    @CreatedBy
    @Column(name = COL_CREATED_BY)
    private String createdBy;

    @CreatedDate
    @Column(name = COL_CREATED_DATE)
    private LocalDateTime createdDate;

    @LastModifiedBy
    @Column(name = COL_LAST_MODIFIED_BY)
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = COL_LAST_MODIFIED_DATE)
    private LocalDateTime lastModifiedDate;
}
