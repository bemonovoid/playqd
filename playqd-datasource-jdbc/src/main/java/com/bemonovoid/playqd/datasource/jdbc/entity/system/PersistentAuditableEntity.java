package com.bemonovoid.playqd.datasource.jdbc.entity.system;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.domain.Persistable;
import org.springframework.util.StringUtils;

@Getter
@Setter
@MappedSuperclass
public abstract class PersistentAuditableEntity extends AuditableEntity implements Persistable<UUID> {

    public static final String COL_PK_ID = "ID";

    @Id
    @GeneratedValue
    @Column(name = COL_PK_ID, updatable = false, nullable = false)
    @Type(type="org.hibernate.type.UUIDCharType")
    private UUID id;

    @Override
    public boolean isNew() {
        return getId() != null;
    }

    public final String getUUID() {
        return getId().toString();
    }

    public final void setUUID(String uuid) {
        if (StringUtils.hasText(uuid)) {
            setId(UUID.fromString(uuid));
        }
    }

}
