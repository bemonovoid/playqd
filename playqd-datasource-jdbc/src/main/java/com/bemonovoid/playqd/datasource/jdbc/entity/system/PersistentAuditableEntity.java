package com.bemonovoid.playqd.datasource.jdbc.entity.system;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.bemonovoid.playqd.datasource.jdbc.entity.system.AuditableEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

@Getter
@Setter
@MappedSuperclass
public abstract class PersistentAuditableEntity<ID extends Serializable>
        extends AuditableEntity implements Persistable<ID> {

    public static final String COL_PK_ID = "ID";

    @Id
    @Column(name = COL_PK_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ID id;

    @Override
    public boolean isNew() {
        return getId() != null;
    }

}
