package com.coursework.belarus_map.service;

import com.coursework.belarus_map.condition.ColorGradientValue;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class GradientService {

    public Color calculateColor(List<ColorGradientValue> colorPoints, double currentValue) {
        validateColorPoints(colorPoints);

        List<ColorGradientValue> sortedPoints = getSortedPoints(colorPoints);

        // Граничные случаи
        if (currentValue <= sortedPoints.get(0).value()) {
            return sortedPoints.get(0).color();
        }
        if (currentValue >= sortedPoints.get(sortedPoints.size() - 1).value()) {
            return sortedPoints.get(sortedPoints.size() - 1).color();
        }

        // Поиск сегмента для интерполяции
        for (int i = 0; i < sortedPoints.size() - 1; i++) {
            ColorGradientValue start = sortedPoints.get(i);
            ColorGradientValue end = sortedPoints.get(i + 1);

            if (currentValue >= start.value() && currentValue <= end.value()) {
                float progress = (float) ((currentValue - start.value())
                        / (end.value() - start.value()));
                return interpolate(start.color(), end.color(), progress);
            }
        }

        return sortedPoints.get(0).color(); // fallback
    }

    public Color interpolate(Color start, Color end, float progress) {
        int red = (int) (start.getRed() + (end.getRed() - start.getRed()) * progress);
        int green = (int) (start.getGreen() + (end.getGreen() - start.getGreen()) * progress);
        int blue = (int) (start.getBlue() + (end.getBlue() - start.getBlue()) * progress);

        return new Color(
                clamp(red, 0, 255),
                clamp(green, 0, 255),
                clamp(blue, 0, 255)
        );
    }

    public List<Color> interpolate(List<Color> colors, int steps) {
        List<Color> result = new ArrayList<>();
        if (colors.isEmpty()) return result;
        if (colors.size() == 1) {
            return Collections.nCopies(steps, colors.get(0));
        }

        float segmentSteps = (float) steps / (colors.size() - 1);

        for (int i = 0; i < colors.size() - 1; i++) {
            Color start = colors.get(i);
            Color end = colors.get(i + 1);

            for (int j = 0; j < segmentSteps; j++) {
                float ratio = j / segmentSteps;
                result.add(interpolate(start, end, ratio));
            }
        }

        return result.subList(0, steps);
    }

    private List<ColorGradientValue> getSortedPoints(List<ColorGradientValue> points) {
        List<ColorGradientValue> sorted = new java.util.ArrayList<>(List.copyOf(points));
        sorted.sort(Comparator.comparingDouble(ColorGradientValue::value));
        return sorted;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    private void validateColorPoints(List<ColorGradientValue> colorPoints) {
        if (colorPoints == null || colorPoints.isEmpty()) {
            throw new IllegalArgumentException("Color points list must not be empty");
        }
    }

    public static String colorToHex(Color color) {
        return String.format("#%02x%02x%02x",
                color.getRed(),
                color.getGreen(),
                color.getBlue());
    }
}