package com.transittracker.service;

import com.transittracker.config.Api.TripUpdatesConfig;
import com.transittracker.exception.DataFetchException;
import com.transittracker.exception.ProtobufParseException;
import com.transittracker.model.*;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for fetching and parsing GTFS Realtime data
 */
@Service
public class GtfsRealtimeService {
    private final HttpClient httpClient;

    public GtfsRealtimeService() {
        this.httpClient = TripUpdatesConfig.getHttpClient();
    }

    /**
     * Fetches GTFS Realtime data from the API
     * 
     * @return Raw protobuf data as byte array
     * @throws DataFetchException if the fetch fails
     */
    public byte[] fetchGtfsRealtimeData() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(TripUpdatesConfig.getTripUpdatesUri())
                    .build();

            HttpResponse<byte[]> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() != 200) {
                throw new DataFetchException("Failed to fetch data. Status code: " + response.statusCode());
            }

            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new DataFetchException("Error fetching GTFS Realtime data", e);
        }
    }

    /**
     * Parses protobuf data into domain models
     * 
     * @param data Raw protobuf bytes
     * @return List of Trip domain models
     * @throws ProtobufParseException if parsing fails
     */
    public List<Trip> parseGtfsRealtimeData(byte[] data) {
        try {
            FeedMessage feedMessage = FeedMessage.parseFrom(data);
            List<Trip> trips = new ArrayList<>();

            for (FeedEntity entity : feedMessage.getEntityList()) {
                if (entity.hasTripUpdate()) {
                    Trip trip = convertToTrip(entity);
                    trips.add(trip);
                }
            }

            return trips;
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
            throw new ProtobufParseException("Error parsing protobuf data", e);
        }
    }

    /**
     * Gets the feed header information
     * 
     * @param data Raw protobuf bytes
     * @return FeedHeader domain model
     * @throws ProtobufParseException if parsing fails
     */
    public FeedHeader getFeedHeader(byte[] data) {
        try {
            FeedMessage feedMessage = FeedMessage.parseFrom(data);
            var header = feedMessage.getHeader();
            return new FeedHeader(
                    header.getGtfsRealtimeVersion(),
                    header.getTimestamp());
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
            throw new ProtobufParseException("Error parsing protobuf data", e);
        }
    }

    /**
     * Converts a GTFS FeedEntity to a Trip domain model
     */
    private Trip convertToTrip(FeedEntity entity) {
        TripUpdate tripUpdate = entity.getTripUpdate();
        Trip trip = new Trip();

        // Set trip information
        if (tripUpdate.hasTrip()) {
            var gtfsTrip = tripUpdate.getTrip();
            trip.setTripId(gtfsTrip.getTripId());
            trip.setRouteId(gtfsTrip.getRouteId());
            if (gtfsTrip.hasScheduleRelationship()) {
                trip.setScheduleRelationship(gtfsTrip.getScheduleRelationship().toString());
            }
        }

        // Set vehicle information
        if (tripUpdate.hasVehicle()) {
            var gtfsVehicle = tripUpdate.getVehicle();
            Vehicle vehicle = new Vehicle();
            vehicle.setId(gtfsVehicle.getId());
            if (gtfsVehicle.hasLabel()) {
                vehicle.setLabel(gtfsVehicle.getLabel());
            }
            trip.setVehicle(vehicle);
        }

        // Convert stop time updates
        List<StopTimeUpdate> stopTimeUpdates = new ArrayList<>();
        for (com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate gtfsStopUpdate : tripUpdate
                .getStopTimeUpdateList()) {
            StopTimeUpdate stopUpdate = new StopTimeUpdate();
            stopUpdate.setStopSequence(gtfsStopUpdate.getStopSequence());
            if (gtfsStopUpdate.hasStopId()) {
                stopUpdate.setStopId(gtfsStopUpdate.getStopId());
            }

            if (gtfsStopUpdate.hasArrival()) {
                var arrival = gtfsStopUpdate.getArrival();
                StopTimeEvent arrivalEvent = new StopTimeEvent();
                arrivalEvent.setTime(arrival.getTime());
                if (arrival.hasDelay()) {
                    arrivalEvent.setDelay((int) arrival.getDelay());
                }
                stopUpdate.setArrival(arrivalEvent);
            }

            if (gtfsStopUpdate.hasDeparture()) {
                var departure = gtfsStopUpdate.getDeparture();
                StopTimeEvent departureEvent = new StopTimeEvent();
                departureEvent.setTime(departure.getTime());
                if (departure.hasDelay()) {
                    departureEvent.setDelay((int) departure.getDelay());
                }
                stopUpdate.setDeparture(departureEvent);
            }

            stopTimeUpdates.add(stopUpdate);
        }
        trip.setStopTimeUpdates(stopTimeUpdates);

        return trip;
    }
}
