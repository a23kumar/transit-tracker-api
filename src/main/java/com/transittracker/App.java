package com.transittracker;

import com.transittracker.config.ApiConfig;
import com.transittracker.dto.response.FeedHeaderResponse;
import com.transittracker.dto.response.TripResponse;
import com.transittracker.exception.DataFetchException;
import com.transittracker.exception.ProtobufParseException;
import com.transittracker.model.Trip;
import com.transittracker.service.GtfsRealtimeService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main application entry point for Transit Tracker API
 */
public class App {
    private final GtfsRealtimeService gtfsRealtimeService;

    public App() {
        this.gtfsRealtimeService = new GtfsRealtimeService();
    }

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    public void run() {
        try {
            // Fetch GTFS Realtime data using the service layer
            System.out.println("Fetching GTFS Realtime data from: " + ApiConfig.GTFS_REALTIME_URL);
            byte[] data = gtfsRealtimeService.fetchGtfsRealtimeData();
            System.out.println("Data fetched successfully. Size: " + data.length + " bytes");
            
            // Optionally save to file for debugging
            saveDataToFile(data);
            
            // Parse and display feed header using DTO layer
            FeedHeaderResponse header = FeedHeaderResponse.from(gtfsRealtimeService.getFeedHeader(data));
            displayFeedHeader(header);
            
            // Parse trips using service layer (returns model layer objects)
            List<Trip> trips = gtfsRealtimeService.parseGtfsRealtimeData(data);
            System.out.println("Number of trips: " + trips.size() + "\n");
            
            // Display summary statistics using model layer
            displaySummary(trips);
            
            // Display trips using DTO layer for presentation
            displayTrips(trips);
            
        } catch (DataFetchException e) {
            System.err.println("Error fetching data: " + e.getMessage());
            e.printStackTrace();
        } catch (ProtobufParseException e) {
            System.err.println("Error parsing protobuf data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void saveDataToFile(byte[] data) {
        try {
            Files.write(Path.of("file.pb"), data);
            System.out.println("File saved to: file.pb\n");
        } catch (IOException e) {
            System.err.println("Warning: Could not save file: " + e.getMessage());
        }
    }
    
    private void displaySummary(List<Trip> trips) {
        System.out.println("=== Summary Statistics ===");
        
        // Count unique routes using model layer
        long uniqueRoutes = trips.stream()
                .map(Trip::getRouteId)
                .filter(routeId -> routeId != null && !routeId.isEmpty())
                .distinct()
                .count();
        System.out.println("Unique routes: " + uniqueRoutes);
        
        // Count trips with vehicles
        long tripsWithVehicles = trips.stream()
                .filter(trip -> trip.getVehicle() != null)
                .count();
        System.out.println("Trips with vehicle information: " + tripsWithVehicles);
        
        // Count total stop updates
        long totalStopUpdates = trips.stream()
                .filter(trip -> trip.getStopTimeUpdates() != null)
                .mapToLong(trip -> trip.getStopTimeUpdates().size())
                .sum();
        System.out.println("Total stop updates: " + totalStopUpdates);
        
        System.out.println();
    }

    private void displayFeedHeader(FeedHeaderResponse header) {
        System.out.println("=== Feed Header ===");
        System.out.println("GTFS Realtime Version: " + header.getGtfsRealtimeVersion());
        // Using DateUtils from util layer for additional formatting if needed
        System.out.println("Timestamp: " + header.getFormattedTimestamp() + " (Unix: " + header.getTimestamp() + ")");
        System.out.println();
    }

    private void displayTrips(List<Trip> trips) {
        System.out.println("=== Trips ===");
        // Convert model layer to DTO layer for display
        List<TripResponse> tripResponses = trips.stream()
                .map(TripResponse::from)
                .collect(Collectors.toList());
        
        for (TripResponse response : tripResponses) {
            displayTrip(response);
        }
    }

    private void displayTrip(TripResponse trip) {
        System.out.println("Trip ID: " + trip.getTripId());
        System.out.println("  Route ID: " + trip.getRouteId());
        if (trip.getScheduleRelationship() != null) {
            System.out.println("  Schedule Relationship: " + trip.getScheduleRelationship());
        }
        
        if (trip.getVehicle() != null) {
            System.out.println("  Vehicle ID: " + trip.getVehicle().getId());
            if (trip.getVehicle().getLabel() != null) {
                System.out.println("  Vehicle Label: " + trip.getVehicle().getLabel());
            }
        }
        
        if (trip.getStopTimeUpdates() != null && !trip.getStopTimeUpdates().isEmpty()) {
            System.out.println("  Stop Updates:");
            for (TripResponse.StopTimeUpdateInfo stopUpdate : trip.getStopTimeUpdates()) {
                System.out.println("    Stop Sequence: " + stopUpdate.getStopSequence());
                if (stopUpdate.getStopId() != null) {
                    System.out.println("    Stop ID: " + stopUpdate.getStopId());
                }
                if (stopUpdate.getArrival() != null) {
                    var arrival = stopUpdate.getArrival();
                    System.out.println("    Arrival Time: " + arrival.getFormattedTime() + " (Unix: " + arrival.getUnixTime() + ")");
                    if (arrival.getDelaySeconds() != 0) {
                        System.out.println("    Arrival Delay: " + arrival.getDelaySeconds() + " seconds");
                    }
                }
                if (stopUpdate.getDeparture() != null) {
                    var departure = stopUpdate.getDeparture();
                    System.out.println("    Departure Time: " + departure.getFormattedTime() + " (Unix: " + departure.getUnixTime() + ")");
                    if (departure.getDelaySeconds() != 0) {
                        System.out.println("    Departure Delay: " + departure.getDelaySeconds() + " seconds");
                    }
                }
            }
        }
        System.out.println();
    }
}


