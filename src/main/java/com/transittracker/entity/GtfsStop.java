package com.transittracker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "stops")
public class GtfsStop {
    @Id
    private String stopId;
    private String stopName;
    private Double stopLat;
    private Double stopLon;

    public GtfsStop() {
    }

    public GtfsStop(String stopId, String stopName, Double stopLat, Double stopLon) {
        this.stopId = stopId;
        this.stopName = stopName;
        this.stopLat = stopLat;
        this.stopLon = stopLon;
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

    public Double getStopLat() {
        return stopLat;
    }

    public void setStopLat(Double stopLat) {
        this.stopLat = stopLat;
    }

    public Double getStopLon() {
        return stopLon;
    }

    public void setStopLon(Double stopLon) {
        this.stopLon = stopLon;
    }
}
