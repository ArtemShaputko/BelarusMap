package com.coursework.belarus_map.service;

import com.diozero.ws281xj.LedDriverInterface;
import com.diozero.ws281xj.rpiws281x.WS281x;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StripService implements AutoCloseable {

    private final LedDriverInterface driver1;
    private final LedDriverInterface driver2;
    private final GradientService gradientService;

    public StripService(GradientService gradientService) {
        this.gradientService = gradientService;
        int stripPin1 = 18;
        int brightness1 = 64;
        int numPixels1 = 60;
        log.info("1 pin: {}, brightness: {}, pixels: {}", stripPin1, brightness1, numPixels1);
        int stripPin2 = 12;
        int brightness2 = 64;
        int numPixels2 = 34;
        log.info("2 pin: {}, brightness: {}, pixels: {}", stripPin2, brightness2, numPixels2);
        try {
            driver1 =  new WS281x(stripPin1, brightness1, numPixels1);
            driver2 =  new WS281x(stripPin2, brightness2, numPixels2);
        } catch (RuntimeException e) {
            log.error("Error creating led driver: {}", e.getMessage());
            throw e;
        }
    }

    public void setColor(int pixel, Color color) {
        driver1.setPixelColourRGB(pixel, color.getRed(), color.getGreen(), color.getBlue());
    }

    private void setColor2(int pixel, Color color) {
        driver2.setPixelColourRGB(pixel, color.getRed(), color.getGreen(), color.getBlue());
    }

    public void setColors(Map<Integer, Color> colors) {
        for (Map.Entry<Integer, Color> entry : colors.entrySet()) {
            setColor(entry.getKey(), entry.getValue());
        }
        render();
    }

    public void setGradientColor( List<Color> colors) {
        int numPixels = driver1.getNumPixels();
        List<Color> interpolatedColors = gradientService.interpolate(colors, numPixels);

        for (int i = 0; i < numPixels; i ++) {
            setColor2(i, interpolatedColors.get(i));
        }
        driver2.render();
    }

    public void render() {
        log.info("Rendering strip ...");
        driver1.render();
    }

    @PreDestroy
    @Override
    public void close() {
        driver1.close();
        driver2.close();
    }
}
