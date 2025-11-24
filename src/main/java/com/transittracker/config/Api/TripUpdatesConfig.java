package com.transittracker.config.Api;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;

import com.transittracker.config.Api.Constants.LiveDataProtbufUrls;

/**
 * Configuration class for API settings
 */
public class TripUpdatesConfig {
    public static final URI TRIP_UPDATES_PROTOBUF_URI = URI.create(LiveDataProtbufUrls.TRIP_UPDATES_PROTOBUF_URL);

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
     * Gets the Trip Updates API URI
     */
    public static URI getTripUpdatesUri() {
        return TRIP_UPDATES_PROTOBUF_URI;
    }
}
