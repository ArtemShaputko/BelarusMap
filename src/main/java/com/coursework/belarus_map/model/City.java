package com.coursework.belarus_map.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class City {
    private String name;
    private double latitude;
    private double longitude;
    private String color = "#ff4757";
    private boolean central;
    private Integer pinNum;
}
