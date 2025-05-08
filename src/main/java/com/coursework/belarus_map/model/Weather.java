package com.coursework.belarus_map.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Weather {
    private double temperature;
    private double humidity;
    private double clouds;
    private double windSpeed;
}
