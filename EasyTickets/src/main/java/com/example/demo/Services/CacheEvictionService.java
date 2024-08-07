package com.example.demo.Services;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class CacheEvictionService {
    @CacheEvict(value = "events", allEntries = true)
    public void evictEventsCache() {
        System.out.println("Cache Evicted");
    }
}
