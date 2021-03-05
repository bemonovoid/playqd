package com.bemonovoid.playqd.datasource.jdbc.entity.system;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity {

    public static final String COL_CREATED_BY = "CREATED_BY";
    public static final String COL_CREATED_DATE = "CREATED_DATE";
    public static final String COL_LAST_MODIFIED_BY = "LAST_MODIFIED_BY";
    public static final String COL_LAST_MODIFIED_DATE = "LAST_MODIFIED_DATE";

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
