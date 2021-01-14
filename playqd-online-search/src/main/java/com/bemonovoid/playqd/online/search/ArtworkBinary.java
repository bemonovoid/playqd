package com.bemonovoid.playqd.online.search;

public class ArtworkBinary {

    private final byte[] binaryData;
    private final String mimeType;

    public ArtworkBinary(byte[] binaryData, String mimeType) {
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

    public static ArtworkBinary empty() {
        return new ArtworkBinary(null, null);
    }
}
