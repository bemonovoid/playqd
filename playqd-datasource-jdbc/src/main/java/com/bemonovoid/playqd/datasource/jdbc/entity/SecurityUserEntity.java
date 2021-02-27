package com.bemonovoid.playqd.datasource.jdbc.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

//@Table(name = SecurityUserEntity.TABLE_NAME)
//@Entity
//@Getter
//@Setter
public class SecurityUserEntity extends PersistentAuditableEntity<Long> {

    static final String TABLE_NAME = "SECURITY_USER";

    @Column(name = "NAME")
    private String name;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "ENABLED")
    private boolean enabled;

    @OneToMany(mappedBy = "user")
    private Set<SecurityAuthoritiesEntity> authorities;

}
