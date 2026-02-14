package com.transittracker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "routes")
public class GtfsRoute {
    @Id
    private String routeId;
    private String routeShortName;
    private String routeLongName;
    private Integer routeType;

    public GtfsRoute() {
    }

    public GtfsRoute(String routeId, String routeShortName, String routeLongName, Integer routeType) {
        this.routeId = routeId;
        this.routeShortName = routeShortName;
        this.routeLongName = routeLongName;
        this.routeType = routeType;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getRouteShortName() {
        return routeShortName;
    }

    public void setRouteShortName(String routeShortName) {
        this.routeShortName = routeShortName;
    }

    public String getRouteLongName() {
        return routeLongName;
    }

    public void setRouteLongName(String routeLongName) {
        this.routeLongName = routeLongName;
    }

    public Integer getRouteType() {
        return routeType;
    }

    public void setRouteType(Integer routeType) {
        this.routeType = routeType;
    }
}
