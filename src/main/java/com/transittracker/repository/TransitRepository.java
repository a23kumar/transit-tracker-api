package com.transittracker.repository;

import com.transittracker.model.Trip;
import com.transittracker.model.TripUpdateEvent;
import com.transittracker.model.VehiclePosition;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Collections;
import java.util.List;

@Repository
public class TransitRepository {

    // Volatile reference swap ensures atomic updates — readers always see a
    // complete list
    private volatile List<Trip> trips = Collections.emptyList();
    private volatile List<VehiclePosition> vehiclePositions = Collections.emptyList();
    private final Sinks.Many<TripUpdateEvent> sink = Sinks.many().multicast().onBackpressureBuffer();

    public void updateTrips(List<Trip> newTrips) {
        this.trips = List.copyOf(newTrips);
        sink.tryEmitNext(new TripUpdateEvent(newTrips, System.currentTimeMillis()));
    }

    public void updateVehiclePositions(List<VehiclePosition> newPositions) {
        this.vehiclePositions = List.copyOf(newPositions);
    }

    public List<Trip> getAllTrips() {
        return trips;
    }

    public List<VehiclePosition> getAllVehiclePositions() {
        return vehiclePositions;
    }

    public Flux<TripUpdateEvent> getTripUpdates() {
        return sink.asFlux();
    }
}
