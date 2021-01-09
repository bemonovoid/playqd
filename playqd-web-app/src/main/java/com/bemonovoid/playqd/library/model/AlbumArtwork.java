package com.bemonovoid.playqd.library.model;

public class AlbumArtwork {

    private final byte[] binaryData;
    private final String mimeType;

    public AlbumArtwork(byte[] binaryData, String mimeType) {
        this.binaryData = binaryData;
        this.mimeType = mimeType;
    }

    public byte[] getBinaryData() {
        return this.binaryData;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public boolean isAvailable() {
        return binaryData != null;
    }

    public static AlbumArtwork empty() {
        return new AlbumArtwork(null, null);
    }
}
