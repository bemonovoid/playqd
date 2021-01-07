package com.bemonovoid.playqd.library.model.query;

import java.util.Objects;
import java.util.Optional;

public class SongFilter {

    private final String name;

    public SongFilter(String name) {
        this.name = name;
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SongFilter that = (SongFilter) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
