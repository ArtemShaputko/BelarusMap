package com.coursework.belarus_map.cache;

import lombok.Getter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ColorCache {
    private Map<String, String> cache = new ConcurrentHashMap<>();
    @Getter
    private AtomicLong lastRefresh = new AtomicLong(0);

    public synchronized Map<String, String> getCache() {
        return cache;
    }

    public synchronized void setCache(Map<String, String> cache) {
        this.cache = cache;
        this.lastRefresh = new AtomicLong(System.currentTimeMillis());
    }
}
