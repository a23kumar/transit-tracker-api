package com.transittracker.model;

public class StopTimeUpdate {
    private int stopSequence;
    private String stopId;
    private String stopName;
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

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
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
