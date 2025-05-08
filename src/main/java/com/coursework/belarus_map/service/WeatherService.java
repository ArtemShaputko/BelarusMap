package com.coursework.belarus_map.service;

import com.coursework.belarus_map.cache.WeatherCache;
import com.coursework.belarus_map.configuration.CityConfig;
import com.coursework.belarus_map.dto.WeatherDto;
import com.coursework.belarus_map.model.City;
import com.coursework.belarus_map.model.Weather;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {
    @NonNull
    private CityConfig cityConfig;
    @NonNull
    private WeatherCache cache;
    @Value("${api.url}")
    private String apiUrl;
    @Value("${api.key}")
    private String apiKey;

    @Scheduled(fixedRate = 3600000)
    public void getCurrentWhether() {
        Map<String, Weather> whetherMap = new ConcurrentHashMap<>();
        cityConfig.getList().forEach(city -> whetherMap.put(city.getName(), getCurrentWhether(city)));
        cache.setCache(whetherMap);
        log.info("Updated weather cache, time = {}", cache.getLastRefresh());
    }

    private Weather getCurrentWhether(City city) {
        var restTemplate = new RestTemplate();
        var url = String.format(apiUrl, city.getLatitude(), city.getLongitude(), apiKey);
        var whetherDto = restTemplate.getForObject(url, WeatherDto.class);
        return new Weather(
                whetherDto.getMain().getTemp(),
                whetherDto.getMain().getHumidity(),
                whetherDto.getClouds().getAll(),
                whetherDto.getWind().getSpeed());
    }


}
