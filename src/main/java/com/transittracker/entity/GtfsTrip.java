package com.transittracker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "trips")
public class GtfsTrip {
    @Id
    private String tripId;
    private String routeId;
    private String tripHeadsign;
    private Integer directionId;

    public GtfsTrip() {
    }

    public GtfsTrip(String tripId, String routeId, String tripHeadsign, Integer directionId) {
        this.tripId = tripId;
        this.routeId = routeId;
        this.tripHeadsign = tripHeadsign;
        this.directionId = directionId;
    }

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

    public String getTripHeadsign() {
        return tripHeadsign;
    }

    public void setTripHeadsign(String tripHeadsign) {
        this.tripHeadsign = tripHeadsign;
    }

    public Integer getDirectionId() {
        return directionId;
    }

    public void setDirectionId(Integer directionId) {
        this.directionId = directionId;
    }
}
