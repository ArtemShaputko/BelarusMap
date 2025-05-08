package com.coursework.belarus_map.util;

import com.diozero.api.I2CDevice;
import com.diozero.devices.oled.SSD1306;
import com.diozero.devices.oled.SsdOledCommunicationChannel;
import com.diozero.util.SleepUtil;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class I2cOledMultiplexer implements AutoCloseable {
    private final I2CDevice multiplexer;
    private final I2CDevice device;
    private final Map<Integer, SSD1306> displays;

    public I2cOledMultiplexer(int controller, int MultAddress, int displayAddress) {
        multiplexer = new I2CDevice(controller, MultAddress);
        device = new I2CDevice(controller, displayAddress);
        displays = new HashMap<>();
    }

    public void addDisplay(int channel) {
        if (displays.containsKey(channel)) {
            throw new IllegalArgumentException("channel " + channel + " already exists");
        }
        displays.put(channel, initDisplay(channel));
    }

    public void setPixel(int channel, int x, int y, boolean value) {
        selectChannel(channel);
        displays.get(channel).setPixel(x, y, value);
    }

    public void setDisplay(int channel, boolean value) {
        selectChannel(channel);
        displays.get(channel).setDisplay(value);
    }

    public void display(int channel, BufferedImage image) {
        selectChannel(channel);
        displays.get(channel).display(image);
    }

    private SSD1306 initDisplay(int channel) {
        selectChannel(channel);
        return new SSD1306(
                new SsdOledCommunicationChannel.I2cCommunicationChannel(device), SSD1306.Height.SHORT
        );
    }

    // Выбор канала мультиплексора
    private void selectChannel(int channel) {
        if (channel < 0 || channel > 7) {
            throw new IllegalArgumentException("Invalid channel: " + channel);
        }
        multiplexer.writeByte((byte) (1 << channel));
        SleepUtil.sleepMillis(10);
    }

    @Override
    public void close() {
        for (int channel : displays.keySet()) {
            selectChannel(channel);
            SSD1306 display = displays.get(channel);
            display.clear();
            display.setDisplay(false);
        }
        device.close();
        multiplexer.close();
    }
}
