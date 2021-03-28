package com.bemonovoid.playqd.core.model.settings;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LibrarySettings {

    private boolean rescanAtStartup;
    private boolean deleteMissing;
    private boolean deleteBeforeScan;

}
