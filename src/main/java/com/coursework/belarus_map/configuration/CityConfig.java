package com.coursework.belarus_map.configuration;

import com.coursework.belarus_map.model.City;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "cities")
@EnableConfigurationProperties
@Getter
@Setter
public class CityConfig {
    private List<City> list;
}