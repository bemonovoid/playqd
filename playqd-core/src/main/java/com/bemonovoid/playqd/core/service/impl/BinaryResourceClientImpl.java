package com.bemonovoid.playqd.core.service.impl;

import com.bemonovoid.playqd.core.exception.PlayqdRemoteServiceRequestException;
import com.bemonovoid.playqd.core.service.BinaryResourceClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
class BinaryResourceClientImpl implements BinaryResourceClient {

    private final RestTemplate restTemplate;

    BinaryResourceClientImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public byte[] get(String url) {
        try {
            return restTemplate.getForObject(url, byte[].class);
        } catch (RestClientException e) {
            throw new PlayqdRemoteServiceRequestException("Failed to get remote resource as binary", e);
        }
    }
}
