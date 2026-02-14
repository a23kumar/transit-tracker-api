package com.transittracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;

@Configuration
public class GtfsConfig {

    @Value("${gtfs.realtime.trip-updates-url}")
    private String tripUpdatesUrl;

    @Value("${gtfs.realtime.vehicle-positions-url}")
    private String vehiclePositionsUrl;

    @Value("${gtfs.static.download-urls}")
    private List<String> staticGtfsUrls;

    @Bean
    public HttpClient gtfsHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public String getTripUpdatesUrl() {
        return tripUpdatesUrl;
    }

    public String getVehiclePositionsUrl() {
        return vehiclePositionsUrl;
    }

    public List<String> getStaticGtfsUrls() {
        return staticGtfsUrls;
    }
}
