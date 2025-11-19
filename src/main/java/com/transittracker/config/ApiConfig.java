package com.transittracker.config;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Configuration class for API settings
 */
public class ApiConfig {
    public static final String GTFS_REALTIME_URL = "https://webapps.regionofwaterloo.ca/api/grt-routes/api/tripupdates";
    public static final URI GTFS_REALTIME_URI = URI.create(GTFS_REALTIME_URL);

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * Gets the configured HTTP client
     */
    public static HttpClient getHttpClient() {
        return HTTP_CLIENT;
    }

    /**
     * Gets the GTFS Realtime API URI
     */
    public static URI getGtfsRealtimeUri() {
        return GTFS_REALTIME_URI;
    }
}
