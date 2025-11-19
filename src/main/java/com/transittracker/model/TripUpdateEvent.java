package com.transittracker.model;

import java.util.List;

public class TripUpdateEvent {
    private List<Trip> trips;
    private long timestamp;

    public TripUpdateEvent() {
    }

    public TripUpdateEvent(List<Trip> trips, long timestamp) {
        this.trips = trips;
        this.timestamp = timestamp;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
