package com.bemonovoid.playqd.config;

import java.util.Collection;
import java.util.List;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
//@EnableCaching
//class CacheConfig {

//    @Bean
//    CacheManager cacheManager() {
//        SimpleCacheManager cacheManager = new SimpleCacheManager();
//        cacheManager.setCaches(caches());
//        return cacheManager;
//    }
//
//    private static Collection<Cache> caches() {
//        return List.of(
//                new ConcurrentMapCache("artists"),
//                new ConcurrentMapCache("artist-albums"),
//                new ConcurrentMapCache("album-songs"));
//    }

//}
