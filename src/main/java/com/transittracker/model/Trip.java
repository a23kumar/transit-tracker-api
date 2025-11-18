package com.transittracker.model;

import java.util.List;

/**
 * Domain model representing a transit trip
 */
public class Trip {
    private String tripId;
    private String routeId;
    private String scheduleRelationship;
    private Vehicle vehicle;
    private List<StopTimeUpdate> stopTimeUpdates;

    public Trip() {
    }

    public Trip(String tripId, String routeId) {
        this.tripId = tripId;
        this.routeId = routeId;
    }

    // Getters and setters
    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getScheduleRelationship() {
        return scheduleRelationship;
    }

    public void setScheduleRelationship(String scheduleRelationship) {
        this.scheduleRelationship = scheduleRelationship;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public List<StopTimeUpdate> getStopTimeUpdates() {
        return stopTimeUpdates;
    }

    public void setStopTimeUpdates(List<StopTimeUpdate> stopTimeUpdates) {
        this.stopTimeUpdates = stopTimeUpdates;
    }
}

