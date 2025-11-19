package com.transittracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TransitTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransitTrackerApplication.class, args);
    }
}
