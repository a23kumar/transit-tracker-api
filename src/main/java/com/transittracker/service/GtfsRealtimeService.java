package com.transittracker.service;

import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.transittracker.config.GtfsConfig;
import com.transittracker.exception.DataFetchException;
import com.transittracker.exception.ProtobufParseException;
import com.transittracker.model.*;
import com.transittracker.repository.GtfsRouteRepository;
import com.transittracker.repository.GtfsStopRepository;
import com.transittracker.repository.GtfsTripRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GtfsRealtimeService {

    private static final Logger logger = LoggerFactory.getLogger(GtfsRealtimeService.class);

    private final GtfsConfig gtfsConfig;
    private final HttpClient httpClient;
    private final GtfsRouteRepository routeRepository;
    private final GtfsStopRepository stopRepository;
    private final GtfsTripRepository tripRepository;

    // Volatile ensures the polling thread always sees the latest cache built by
    // refreshCaches()
    private volatile Map<String, String> routeNameCache = new HashMap<>();
    private volatile Map<String, String> stopNameCache = new HashMap<>();
    private volatile Map<String, String> tripHeadsignCache = new HashMap<>();

    public GtfsRealtimeService(GtfsConfig gtfsConfig, HttpClient httpClient,
            GtfsRouteRepository routeRepository,
            GtfsStopRepository stopRepository,
            GtfsTripRepository tripRepository) {
        this.gtfsConfig = gtfsConfig;
        this.httpClient = httpClient;
        this.routeRepository = routeRepository;
        this.stopRepository = stopRepository;
        this.tripRepository = tripRepository;
    }

    public void refreshCaches() {
        routeNameCache = new HashMap<>();
        routeRepository.findAll().forEach(route -> {
            String name = route.getRouteShortName() != null ? route.getRouteShortName()
                    : route.getRouteLongName() != null ? route.getRouteLongName() : route.getRouteId();
            routeNameCache.put(route.getRouteId(), name);
        });

        stopNameCache = new HashMap<>();
        stopRepository.findAll().forEach(stop -> {
            stopNameCache.put(stop.getStopId(), stop.getStopName() != null ? stop.getStopName() : stop.getStopId());
        });

        tripHeadsignCache = new HashMap<>();
        tripRepository.findAll().forEach(trip -> {
            if (trip.getTripHeadsign() != null) {
                tripHeadsignCache.put(trip.getTripId(), trip.getTripHeadsign());
            }
        });

        logger.info("Refreshed GTFS caches: {} routes, {} stops, {} trips",
                routeNameCache.size(), stopNameCache.size(), tripHeadsignCache.size());
    }

    public List<Trip> fetchTripUpdates() {
        byte[] data = fetchProtobufData(gtfsConfig.getTripUpdatesUrl());
        return parseTripUpdates(data);
    }

    public List<VehiclePosition> fetchVehiclePositions() {
        byte[] data = fetchProtobufData(gtfsConfig.getVehiclePositionsUrl());
        return parseVehiclePositions(data);
    }

    private byte[] fetchProtobufData(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() != 200) {
                throw new DataFetchException(
                        "Failed to fetch data from " + url + ". Status code: " + response.statusCode());
            }

            return response.body();
        } catch (DataFetchException e) {
            throw e;
        } catch (Exception e) {
            throw new DataFetchException("Error fetching GTFS Realtime data from " + url, e);
        }
    }

    private List<Trip> parseTripUpdates(byte[] data) {
        try {
            FeedMessage feedMessage = FeedMessage.parseFrom(data);
            List<Trip> trips = new ArrayList<>();

            for (FeedEntity entity : feedMessage.getEntityList()) {
                if (entity.hasTripUpdate()) {
                    trips.add(convertToTrip(entity));
                }
            }

            return trips;
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
            throw new ProtobufParseException("Error parsing trip updates protobuf data", e);
        }
    }

    private List<VehiclePosition> parseVehiclePositions(byte[] data) {
        try {
            FeedMessage feedMessage = FeedMessage.parseFrom(data);
            List<VehiclePosition> positions = new ArrayList<>();

            for (FeedEntity entity : feedMessage.getEntityList()) {
                if (entity.hasVehicle()) {
                    var v = entity.getVehicle();
                    VehiclePosition position = new VehiclePosition();

                    if (v.hasVehicle()) {
                        position.setVehicleId(v.getVehicle().getId());
                        if (v.getVehicle().hasLabel()) {
                            position.setLabel(v.getVehicle().getLabel());
                        }
                    }

                    if (v.hasPosition()) {
                        position.setLatitude((double) v.getPosition().getLatitude());
                        position.setLongitude((double) v.getPosition().getLongitude());
                        if (v.getPosition().hasBearing()) {
                            position.setBearing(v.getPosition().getBearing());
                        }
                        if (v.getPosition().hasSpeed()) {
                            position.setSpeed(v.getPosition().getSpeed());
                        }
                    }

                    if (v.hasTimestamp()) {
                        position.setTimestamp(v.getTimestamp());
                    }

                    if (v.hasTrip()) {
                        position.setTripId(v.getTrip().getTripId());
                        String routeId = v.getTrip().getRouteId();
                        position.setRouteId(routeId);
                        position.setRouteName(routeNameCache.get(routeId));
                    }

                    positions.add(position);
                }
            }

            return positions;
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
            throw new ProtobufParseException("Error parsing vehicle positions protobuf data", e);
        }
    }

    private Trip convertToTrip(FeedEntity entity) {
        var tripUpdate = entity.getTripUpdate();
        Trip trip = new Trip();

        if (tripUpdate.hasTrip()) {
            var gtfsTrip = tripUpdate.getTrip();
            String tripId = gtfsTrip.getTripId();
            String routeId = gtfsTrip.getRouteId();

            trip.setTripId(tripId);
            trip.setRouteId(routeId);
            trip.setRouteName(routeNameCache.get(routeId));
            trip.setTripHeadsign(tripHeadsignCache.get(tripId));

            if (gtfsTrip.hasScheduleRelationship()) {
                trip.setScheduleRelationship(gtfsTrip.getScheduleRelationship().toString());
            }
        }

        if (tripUpdate.hasVehicle()) {
            var gtfsVehicle = tripUpdate.getVehicle();
            Vehicle vehicle = new Vehicle();
            vehicle.setId(gtfsVehicle.getId());
            if (gtfsVehicle.hasLabel()) {
                vehicle.setLabel(gtfsVehicle.getLabel());
            }
            trip.setVehicle(vehicle);
        }

        List<StopTimeUpdate> stopTimeUpdates = new ArrayList<>();
        for (var gtfsStopUpdate : tripUpdate.getStopTimeUpdateList()) {
            StopTimeUpdate stopUpdate = new StopTimeUpdate();
            stopUpdate.setStopSequence(gtfsStopUpdate.getStopSequence());

            if (gtfsStopUpdate.hasStopId()) {
                String stopId = gtfsStopUpdate.getStopId();
                stopUpdate.setStopId(stopId);
                stopUpdate.setStopName(stopNameCache.get(stopId));
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
