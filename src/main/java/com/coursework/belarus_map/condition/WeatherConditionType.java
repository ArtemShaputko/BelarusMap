package com.coursework.belarus_map.condition;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.Color;
import java.util.List;

@AllArgsConstructor
@Getter
public enum WeatherConditionType {
    TEMPERATURE(List.of(
            new ColorGradientValue(
                    -20f,
                    Color.decode("#0000ff"),
                    1),
            new ColorGradientValue(
                    -10f,
                    Color.decode("#37ffff"),
                    20),
            new ColorGradientValue(
                    0,
                    Color.decode("#65ff65"),
                    40),
            new ColorGradientValue(
                    10f,
                    Color.decode("#ffff37"),
                    60),
            new ColorGradientValue(
                    20f,
                    Color.decode("#ffac14"),
                    80),
            new ColorGradientValue(
                    30f,
                    Color.RED,
                    99)),
            "Температура (°C)"),
    HUMIDITY(List.of(
            new ColorGradientValue(
                    20f,
                    Color.decode("#eff6db"),
                    1),
            new ColorGradientValue(
                    30f,
                    Color.decode("#bef0b3"),
                    12.5),
            new ColorGradientValue(
                    40f,
                    Color.decode("#93f0c7"),
                    25),
            new ColorGradientValue(
                    50f,
                    Color.decode("#81e7df"),
                    37.5),
            new ColorGradientValue(
                    60f,
                    Color.decode("#76d6e3"),
                    50),
            new ColorGradientValue(
                    70f,
                    Color.decode("#65b2de"),
                    62.5),
            new ColorGradientValue(
                    80f,
                    Color.decode("#6394de"),
                    75),
            new ColorGradientValue(
                    90f,
                    Color.decode("#6b7fc1"),
                    87.5),
            new ColorGradientValue(
                    100f,
                    Color.decode("#6f7ba7"),
                    99)),
            "Влажность (%)"
    ),
    CLOUDS(List.of(
            new ColorGradientValue(
                    0f,
                    Color.decode("#ffff00"),
                    1),
            new ColorGradientValue(
                    100f,
                    Color.decode("#ffffff"),
                    99)),
            "Облачность (%)"
    ),
    WIND(List.of(
            new ColorGradientValue(
                    0f,
                    Color.decode("#8383b5"),
                    1),
            new ColorGradientValue(
                    1f,
                    Color.decode("#4d8fc1"),
                    10),
            new ColorGradientValue(
                    2f,
                    Color.decode("#65c8cb"),
                    20),
            new ColorGradientValue(
                    3f,
                    Color.decode("#4dacb5"),
                    30),
            new ColorGradientValue(
                    4f,
                    Color.decode("#4f9175"),
                    40),
            new ColorGradientValue(
                    5f,
                    Color.decode("#75bc7e"),
                    50),
            new ColorGradientValue(
                    10f,
                    Color.decode("#e8e55d"),
                    60),
            new ColorGradientValue(
                    15f,
                    Color.decode("#fcdca9"),
                    70),
            new ColorGradientValue(
                    20f,
                    Color.decode("#f19c51"),
                    80),
            new ColorGradientValue(
                    30f,
                    Color.decode("#e8515f"),
                    90),
            new ColorGradientValue(
                    50f,
                    Color.decode("#896b68"),
                    99)),
            "Скорость ветра (м/с)"
    );

    private final List<ColorGradientValue> gradientValues;
    private final String label;

    public List<Color> getGradientValuesColors() {
        return gradientValues.stream().map(ColorGradientValue::color).toList();
    }
}
