package com.coursework.belarus_map.condition;

import java.awt.*;

public record ColorGradientValue(double value, Color color, double position) {
    public String getColorHex() {
        return String.format("#%06x", color.getRGB() & 0xFFFFFF);
    }
    public String getPositionCSS() {
        return String.format("%.0f%%", position);
    }
    public String getRoundedValue() {
        return String.format("%.0f", value); // Округляем до целого
    }
}
