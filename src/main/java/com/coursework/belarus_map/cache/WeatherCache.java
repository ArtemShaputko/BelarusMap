package com.coursework.belarus_map.cache;

import com.coursework.belarus_map.model.Weather;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class WeatherCache {
    private Map<String, Weather> cache = new ConcurrentHashMap<>();
    @Getter
    private AtomicLong lastRefresh = new AtomicLong(0);

    public synchronized Map<String, Weather> getCache() {
        return cache;
    }

    public synchronized void setCache(Map<String, Weather> cache) {
        this.cache = cache;
        this.lastRefresh = new AtomicLong(System.currentTimeMillis());
    }
}
