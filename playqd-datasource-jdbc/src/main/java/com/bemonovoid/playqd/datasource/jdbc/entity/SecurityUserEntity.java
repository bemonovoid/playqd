package com.bemonovoid.playqd.datasource.jdbc.entity;

import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.bemonovoid.playqd.datasource.jdbc.entity.system.AuditableEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = SecurityUserEntity.TABLE_NAME)
public class SecurityUserEntity extends AuditableEntity {

    static final String TABLE_NAME = "USER";
    static final String AUTHORITIES_TABLE_NAME = "AUTHORITIES";

    static final String COL_PK_ID = "USERNAME";
    static final String COL_PASSWORD = "PASSWORD";
    static final String COL_ENABLED = "ENABLED";
    static final String COL_AUTHORITIES_AUTHORITY = "AUTHORITY";

    @Id
    @Column(name = COL_PK_ID)
    private String id;

    @Column(name = COL_PASSWORD)
    private String password;

    @Column(name = COL_ENABLED)
    private boolean enabled;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = AUTHORITIES_TABLE_NAME, joinColumns = @JoinColumn(name = COL_PK_ID))
    @Column(name = COL_AUTHORITIES_AUTHORITY)
    private Set<String> authorities;

}
