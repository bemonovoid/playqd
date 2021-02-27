package com.bemonovoid.playqd.datasource.jdbc.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

//@Table(name = SecurityAuthoritiesEntity.TABLE_NAME)
//@Entity
//@Getter
//@Setter
public class SecurityAuthoritiesEntity extends PersistentAuditableEntity<Long> {

    static final String TABLE_NAME = "SECURITY_AUTHORITIES";

    @ManyToOne(fetch = FetchType.LAZY)
    private SecurityUserEntity user;
}
