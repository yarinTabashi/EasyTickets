package com.example.demo.Services;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

// This class is used to evict the cache. It is used to clear the cache when the last ticket of event is booked.
@Service
public class CacheEvictionService {
    @CacheEvict(value = "events", allEntries = true)
    public void evictEventsCache() {
        System.out.println("Cache Evicted");
    }
}
