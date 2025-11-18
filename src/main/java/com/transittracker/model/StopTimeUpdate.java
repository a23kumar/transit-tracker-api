package com.transittracker.model;

/**
 * Domain model representing a stop time update for a trip
 */
public class StopTimeUpdate {
    private int stopSequence;
    private String stopId;
    private StopTimeEvent arrival;
    private StopTimeEvent departure;

    public StopTimeUpdate() {
    }

    public int getStopSequence() {
        return stopSequence;
    }

    public void setStopSequence(int stopSequence) {
        this.stopSequence = stopSequence;
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public StopTimeEvent getArrival() {
        return arrival;
    }

    public void setArrival(StopTimeEvent arrival) {
        this.arrival = arrival;
    }

    public StopTimeEvent getDeparture() {
        return departure;
    }

    public void setDeparture(StopTimeEvent departure) {
        this.departure = departure;
    }
}

