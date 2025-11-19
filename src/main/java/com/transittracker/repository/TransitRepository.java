package com.transittracker.repository;

import com.transittracker.model.Trip;
import com.transittracker.model.TripUpdateEvent;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class TransitRepository {

    private final List<Trip> trips = new CopyOnWriteArrayList<>();
    private final Sinks.Many<TripUpdateEvent> sink = Sinks.many().multicast().onBackpressureBuffer();

    public void updateTrips(List<Trip> newTrips) {
        trips.clear();
        trips.addAll(newTrips);
        sink.tryEmitNext(new TripUpdateEvent(newTrips, System.currentTimeMillis()));
    }

    public List<Trip> getAllTrips() {
        return new ArrayList<>(trips);
    }

    public Flux<TripUpdateEvent> getTripUpdates() {
        return sink.asFlux();
    }
}
