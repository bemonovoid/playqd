package com.bemonovoid.playqd.service;

import com.bemonovoid.playqd.core.service.BinaryResourceProducer;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
class BinaryResourceProducerImpl implements BinaryResourceProducer {

    private final RestTemplate restTemplate;

    BinaryResourceProducerImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public byte[] toBinary(String url) {
        return restTemplate.getForObject(url, byte[].class);
    }
}
