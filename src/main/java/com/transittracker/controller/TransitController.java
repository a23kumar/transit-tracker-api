package com.transittracker.controller;

import com.transittracker.entity.GtfsRoute;
import com.transittracker.entity.GtfsStop;
import com.transittracker.model.Trip;
import com.transittracker.model.TripUpdateEvent;
import com.transittracker.model.VehiclePosition;
import com.transittracker.repository.GtfsRouteRepository;
import com.transittracker.repository.GtfsStopRepository;
import com.transittracker.repository.TransitRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class TransitController {

    private final TransitRepository transitRepository;
    private final GtfsRouteRepository routeRepository;
    private final GtfsStopRepository stopRepository;

    public TransitController(TransitRepository transitRepository,
            GtfsRouteRepository routeRepository,
            GtfsStopRepository stopRepository) {
        this.transitRepository = transitRepository;
        this.routeRepository = routeRepository;
        this.stopRepository = stopRepository;
    }

    @QueryMapping
    public List<Trip> trips(@Argument String routeId, @Argument String vehicleId) {
        return transitRepository.getAllTrips().stream()
                .filter(trip -> routeId == null || routeId.equals(trip.getRouteId()))
                .filter(trip -> vehicleId == null
                        || (trip.getVehicle() != null && vehicleId.equals(trip.getVehicle().getId())))
                .collect(Collectors.toList());
    }

    @QueryMapping
    public List<VehiclePosition> vehiclePositions(@Argument String routeId) {
        return transitRepository.getAllVehiclePositions().stream()
                .filter(pos -> routeId == null || routeId.equals(pos.getRouteId()))
                .collect(Collectors.toList());
    }

    @QueryMapping
    public List<GtfsRoute> routes() {
        return StreamSupport.stream(routeRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public List<GtfsStop> stops(@Argument String routeId) {
        // TODO: If routeId filtering is needed, this would require a join through
        // stop_times + trips.
        // For now, return all stops.
        return StreamSupport.stream(stopRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @SubscriptionMapping
    public Flux<TripUpdateEvent> feedUpdates(@Argument String routeId) {
        return transitRepository.getTripUpdates()
                .map(event -> {
                    if (routeId == null) {
                        return event;
                    } else {
                        List<Trip> filtered = event.getTrips().stream()
                                .filter(trip -> routeId.equals(trip.getRouteId()))
                                .collect(Collectors.toList());
                        return new TripUpdateEvent(filtered, event.getTimestamp());
                    }
                });
    }
}
