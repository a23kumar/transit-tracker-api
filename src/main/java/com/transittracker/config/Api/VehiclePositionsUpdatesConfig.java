package com.transittracker.config.Api;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;

public class VehiclePositionsUpdatesConfig {
    private static final String VEHICLE_UPDATES_PROTOBUF_URL = "https://webapps.regionofwaterloo.ca/api/grt-routes/api/vehiclepositions";
    public static final URI VEHICLE_UPDATES_PROTOBUF_URI = URI.create(VEHICLE_UPDATES_PROTOBUF_URL);

    private static HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    /**
     * Gets the configured HTTP client for vehicle positions updates
     */
    public static HttpClient getVehiclePositionsHttpClient() {
        return httpClient;
    }

    /**
     * Gets the configured URI for vehicle positions updates
     */
    public static URI getVehiclePositionsUri() {
        return VEHICLE_UPDATES_PROTOBUF_URI;
    }
}
