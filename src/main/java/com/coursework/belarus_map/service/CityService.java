package com.coursework.belarus_map.service;

import com.coursework.belarus_map.cache.ColorCache;
import com.coursework.belarus_map.cache.WeatherCache;
import com.coursework.belarus_map.configuration.CityConfig;
import com.coursework.belarus_map.model.City;
import com.coursework.belarus_map.condition.WeatherConditionType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
@Slf4j
public class CityService {
    CityConfig cityConfig;
    GradientService gradientService;
    WeatherCache weatherCache;
    StripService stripService;
    ColorCache temperatureCache;
    ColorCache humidityCache;
    ColorCache windCache;
    ColorCache cloudsCache;

    public void setCitiesColor(WeatherConditionType type) {
        if (weatherCache.getLastRefresh().get() == 0) {
            return;
        }
        switch (type) {
            case TEMPERATURE -> setTemperatureColor();
            case HUMIDITY -> setHumidityColor();
            case WIND -> setWindColor();
            case CLOUDS -> setCloudsColor();
        }
    }

    public void setStripColors(WeatherConditionType type) {
        log.info("Setting strip colors");
        cityConfig.getList().forEach(city -> {
                    if(city.getColor() == null) {
                        log.info("city {} color is null", city.getName());
                    }
                    if (city.getPinNum() != null && city.getColor() != null) {
                        log.info("Setting strip color for city {}", city.getName());
                        stripService.setColor(city.getPinNum(), Color.decode(city.getColor()));
                    }
                }
        );
        stripService.setGradientColor(type.getGradientValuesColors());
    }

    private void setTemperatureColor() {
        if (weatherCache.getLastRefresh().get() < temperatureCache.getLastRefresh().get()) {
            cityConfig.getList().forEach(city -> city.setColor(temperatureCache.getCache().get(city.getName())));
            return;
        }
        Map<String, String> newCache = new ConcurrentHashMap<>();
        cityConfig.getList().forEach(city -> {
                    setCityColor(
                            city,
                            weatherCache.getCache().get(city.getName()).getTemperature() - 273.15,
                            WeatherConditionType.TEMPERATURE
                    );
                    newCache.put(city.getName(), city.getColor());
                }
        );
        temperatureCache.setCache(newCache);
    }

    private void setHumidityColor() {
        if (weatherCache.getLastRefresh().get() < humidityCache.getLastRefresh().get()) {
            cityConfig.getList().forEach(city -> city.setColor(humidityCache.getCache().get(city.getName())));
            return;
        }
        Map<String, String> newCache = new ConcurrentHashMap<>();
        cityConfig.getList().forEach(city -> {
                    setCityColor(
                            city,
                            weatherCache.getCache().get(city.getName()).getHumidity(),
                            WeatherConditionType.HUMIDITY
                    );
                    newCache.put(city.getName(), city.getColor());
                }
        );
        humidityCache.setCache(newCache);
    }

    private void setWindColor() {
        if (weatherCache.getLastRefresh().get() < windCache.getLastRefresh().get()) {
            cityConfig.getList().forEach(city -> city.setColor(windCache.getCache().get(city.getName())));
            return;
        }
        Map<String, String> newCache = new ConcurrentHashMap<>();
        cityConfig.getList().forEach(city -> {
                    setCityColor(
                            city,
                            weatherCache.getCache().get(city.getName()).getWindSpeed(),
                            WeatherConditionType.WIND
                    );
                    newCache.put(city.getName(), city.getColor());
                }
        );
        windCache.setCache(newCache);
    }

    private void setCloudsColor() {
        if (weatherCache.getLastRefresh().get() < cloudsCache.getLastRefresh().get()) {
            cityConfig.getList().forEach(city -> city.setColor(cloudsCache.getCache().get(city.getName())));
            return;
        }
        Map<String, String> newCache = new ConcurrentHashMap<>();
        cityConfig.getList().forEach(city -> {
                    setCityColor(
                            city,
                            weatherCache.getCache().get(city.getName()).getClouds(),
                            WeatherConditionType.CLOUDS
                    );
                    newCache.put(city.getName(), city.getColor());
                }
        );
        cloudsCache.setCache(newCache);
    }

    private void setCityColor(City city, double value, WeatherConditionType type) {
        city.setColor(
                GradientService.colorToHex(
                        gradientService.calculateColor(
                                type.getGradientValues(),
                                value
                        )
                )
        );
    }
}
