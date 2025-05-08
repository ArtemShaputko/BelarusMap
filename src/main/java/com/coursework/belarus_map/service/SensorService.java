package com.coursework.belarus_map.service;

import com.coursework.belarus_map.cache.WeatherCache;
import com.coursework.belarus_map.model.Parameter;
import com.coursework.belarus_map.model.Weather;
import com.coursework.belarus_map.util.I2cOledMultiplexer;
import com.diozero.api.*;
import com.diozero.internal.spi.GpioDeviceFactoryInterface;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class SensorService implements AutoCloseable {
    private static final int[] PINS = {22, 27, 23, 17, 24, 25};
    /*
    22       27         23        17         24        25
    "Брест", "Гродно", "Витебск", "Гомель", "Могилёв", "Минск"
     */
    private static final String[] NAMES = {"Брест", "Гродно", "Витебск", "Гомель", "Могилёв", "Минск"};
    private final List<DigitalInputDevice> sensors = new ArrayList<>();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(6);
    private final WeatherCache cache;
    private final int[] displayStates = new int[6];
    private final AtomicBoolean[] scheduledToReset = new AtomicBoolean[6];

    public SensorService(GpioDeviceFactoryInterface deviceFactory,
                         WeatherCache cache,
                         I2cOledMultiplexer multiplexer) {
        this.cache = cache;

        // Инициализируем флаги обработки
        for (int i = 0; i < scheduledToReset.length; i++) {
            scheduledToReset[i] = new AtomicBoolean(false);
        }

        log.info("Initializing SensorService with:");
        log.info("Device factory: {}", deviceFactory.getClass().getName());
        log.info("Pins: {}", Arrays.toString(PINS));

        try {
            for (int i = 0; i < PINS.length; i++) {
                final int displayIndex = i;
                DigitalInputDevice sensor = new DigitalInputDevice(
                        PINS[i],
                        GpioPullUpDown.PULL_DOWN,
                        GpioEventTrigger.RISING
                );

                sensors.add(sensor);

                sensor.addListener(event -> handleButtonPress(displayIndex, multiplexer));
            }
            log.info("All buttons initialized successfully");
        } catch (RuntimeIOException e) {
            log.error("Sensor initialization error", e);
            close();
            throw new BeanCreationException("Sensor service creation failed", e);
        }
    }

    private void handleButtonPress(int displayIndex, I2cOledMultiplexer multiplexer) {
            log.info("Button pressed for display {}", displayIndex);
            updateDisplayState(displayIndex);
            showDisplay(displayIndex, multiplexer);
            scheduleDisplayReset(displayIndex, multiplexer);
    }

    private void showDisplay(int displayIndex, I2cOledMultiplexer multiplexer) {

        try {
            multiplexer.setDisplay(displayIndex, true);
            var parameter = getParameter(NAMES[displayIndex], displayStates[displayIndex]);
            drawString(multiplexer, displayIndex, parameter);
        } catch (RuntimeIOException e) {
            log.error("Display {} error", displayIndex, e);
        } catch (IOException e) {
            log.error("Display {} IOException", displayIndex, e);
            drawString(multiplexer, displayIndex, new Parameter(NAMES[displayIndex], null));
        }
    }

    private void scheduleDisplayReset(int displayIndex, I2cOledMultiplexer multiplexer) {
        if (scheduledToReset[displayIndex].get()) {
            log.info("No reset scheduling for display {}", displayIndex);
            return;
        }
        scheduledToReset[displayIndex].set(true);
        executor.schedule(() -> {
            try {
                multiplexer.setDisplay(displayIndex, false);
                displayStates[displayIndex] = 0;
                log.debug("Display {} reset", displayIndex);
            } catch (RuntimeIOException e) {
                log.error("Display {} reset error", displayIndex, e);
            } finally {
                scheduledToReset[displayIndex].set(false);
            }
        }, 20, TimeUnit.SECONDS);
    }

    private void updateDisplayState(int displayIndex) {
        displayStates[displayIndex] = (displayStates[displayIndex] + 1) % 5;
    }

    private static String formatDouble(double number) {
        DecimalFormat df = new DecimalFormat(
                "#.##",
                new DecimalFormatSymbols(Locale.US) // Для гарантии использования точки
        );
        return df.format(number);
    }

    private Parameter getParameter(String city, int num) throws IOException {
        var cityParam = new Parameter(city, ImageIO.read(
                Objects.requireNonNull(getClass().getResource("/images/city.png"))));
        if (cache == null) {
            log.info("cache base is null");
            return cityParam;
        }
        if (cache.getCache() != null) {
            Weather param = cache.getCache().get(city);
            if (param == null) {
                log.info("parameter is null");
                return cityParam;
            }

            log.info("getting parameter: {}", param);
            return switch (num) {
                case 0 -> cityParam;
                case 1 -> new Parameter(formatDouble(param.getTemperature() - 273.15) + "°C", ImageIO.read(
                                Objects.requireNonNull(getClass().getResource("/images/temp.png"))));
                case 2 -> new Parameter(formatDouble(param.getHumidity()) + "%", ImageIO.read(
                        Objects.requireNonNull(getClass().getResource("/images/humidity.png"))));
                case 3 -> new Parameter(formatDouble(param.getWindSpeed()) + "м/с", ImageIO.read(
                        Objects.requireNonNull(getClass().getResource("/images/wind.png"))));
                case 4 -> new Parameter(formatDouble(param.getClouds()) + "%", ImageIO.read(
                        Objects.requireNonNull(getClass().getResource("/images/cloud.png"))));
                default -> throw new IllegalStateException("Unexpected value: " + num);
            };
        } else {
            log.info("cache is null");
            return cityParam;
        }
    }

    private void drawString(I2cOledMultiplexer multiplexer, int index, Parameter parameter) {
        BufferedImage image = new BufferedImage(128, 32, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = image.createGraphics();

        if (parameter.image() != null) {
            g.drawImage(parameter.image(), 0, 0, 32, 32, null);
        }

        g.setFont(new Font("Times New Roman", Font.PLAIN, 18));

        // Сдвигаем текст вправо если есть иконка
        int textX = (parameter.image() != null) ? 40 : 16;
        g.drawString(parameter.value(), textX, 25);

        g.dispose();
        multiplexer.display(index, image);
    }

    @PreDestroy
    @Override
    public void close() {
        log.info("Shutting down sensor service");
        sensors.forEach(sensor -> {
            try {
                sensor.close();
            } catch (RuntimeIOException e) {
                log.warn("Error closing sensor", e);
            }
        });
        executor.shutdownNow();
    }
}
