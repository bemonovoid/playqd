package com.bemonovoid.playqd.config;

public class AppProperties {

    private MusicDirectoryProperties library;

    public MusicDirectoryProperties getLibrary() {
        return library;
    }

    void setLibrary(MusicDirectoryProperties library) {
        this.library = library;
    }

    public static class MusicDirectoryProperties {

        private String location;

        public String getLocation() {
            return location;
        }

        void setLocation(String location) {
            this.location = location;
        }
    }
}
