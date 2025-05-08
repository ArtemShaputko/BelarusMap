package com.coursework.belarus_map.configuration;

import com.coursework.belarus_map.util.I2cOledMultiplexer;
import com.diozero.internal.provider.builtin.DefaultDeviceFactory;
import com.diozero.internal.spi.GpioDeviceFactoryInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class GpioConfig {
    @Value("${i2c.address.display}")
    private int displayAddress;
    @Value("${i2c.address.multiplexer}")
    private int multiplexerAddress;


    @Bean(destroyMethod = "close")
    public GpioDeviceFactoryInterface deviceFactory() {
        return new DefaultDeviceFactory();
    }

    @Bean(destroyMethod = "close")
    public I2cOledMultiplexer multiplexer() {
        log.info("displayAddress: {}, multiplexerAddress: {}", displayAddress, multiplexerAddress);
        I2cOledMultiplexer multiplexer = new I2cOledMultiplexer(1, multiplexerAddress, displayAddress);
        for(int i = 0; i < 6; i++) {
            log.info("initializing display: {}", i);
            multiplexer.addDisplay(i);
            multiplexer.setDisplay(i, false);
        }
        return multiplexer;
    }
}