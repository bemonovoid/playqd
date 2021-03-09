package com.bemonovoid.playqd.core.helpers;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.bemonovoid.playqd.core.model.LibraryResourceId;
import com.bemonovoid.playqd.core.model.ResourceTarget;

public abstract class ResourceIdHelper {

    private static final String RESOURCE_ID_DELIMITER = "::";

    public static String encode(LibraryResourceId resource) {
        String resourceId = String.format("%s::%s::%s", resource.getId(), resource.getTarget(), resource.getAuthToken());
        return Base64.getEncoder().encodeToString(resourceId.getBytes(StandardCharsets.UTF_8));
    }

    public static LibraryResourceId decode(String resourceIdEncoded) {
        String resourceIdDecoded = new String(Base64.getDecoder().decode(resourceIdEncoded), StandardCharsets.UTF_8);
        String[] parts = resourceIdDecoded.split(RESOURCE_ID_DELIMITER);
        return new LibraryResourceId(Long.parseLong(parts[0]), ResourceTarget.valueOf(parts[1]), parts[2]);
    }


}
