package com.coursework.belarus_map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BelarusMapApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(BelarusMapApplication.class, args);
    }

}