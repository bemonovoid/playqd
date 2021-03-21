package com.bemonovoid.playqd.datasource.jdbc.repository;

import java.util.UUID;

import com.bemonovoid.playqd.datasource.jdbc.entity.LibrarySettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibrarySettingsRepository extends JpaRepository<LibrarySettingsEntity, UUID> {

}
