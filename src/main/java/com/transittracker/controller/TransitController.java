package com.transittracker.controller;

import com.transittracker.model.Trip;
import com.transittracker.model.TripUpdateEvent;
import com.transittracker.repository.TransitRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.List;

@Controller
public class TransitController {

    private final TransitRepository transitRepository;

    public TransitController(TransitRepository transitRepository) {
        this.transitRepository = transitRepository;
    }

    @QueryMapping
    public List<Trip> trips(@Argument String routeId, @Argument String vehicleId) {
        List<Trip> allTrips = transitRepository.getAllTrips();
        return allTrips.stream()
                .filter(trip -> routeId == null || (trip.getRouteId() != null && trip.getRouteId().equals(routeId)))
                .filter(trip -> vehicleId == null || (trip.getVehicle() != null && trip.getVehicle().getId() != null
                        && trip.getVehicle().getId().equals(vehicleId)))
                .toList();
    }

    @SubscriptionMapping
    public Flux<TripUpdateEvent> feedUpdates(@Argument String routeId) {
        return transitRepository.getTripUpdates()
                .map(event -> {
                    if (routeId == null) {
                        return event;
                    }
                    List<Trip> filteredTrips = event.getTrips().stream()
                            .filter(trip -> trip.getRouteId() != null && trip.getRouteId().equals(routeId))
                            .toList();
                    return new TripUpdateEvent(filteredTrips, event.getTimestamp());
                });
    }
}
