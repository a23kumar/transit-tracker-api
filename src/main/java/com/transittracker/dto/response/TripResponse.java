package com.transittracker.dto.response;

import com.transittracker.model.Trip;
import com.transittracker.model.StopTimeUpdate;
import com.transittracker.model.Vehicle;
import com.transittracker.util.DateUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for trip response
 */
public class TripResponse {
    private String tripId;
    private String routeId;
    private String scheduleRelationship;
    private VehicleInfo vehicle;
    private List<StopTimeUpdateInfo> stopTimeUpdates;

    public TripResponse() {
    }

    public static TripResponse from(Trip trip) {
        TripResponse response = new TripResponse();
        response.setTripId(trip.getTripId());
        response.setRouteId(trip.getRouteId());
        response.setScheduleRelationship(trip.getScheduleRelationship());

        if (trip.getVehicle() != null) {
            response.setVehicle(VehicleInfo.from(trip.getVehicle()));
        }

        if (trip.getStopTimeUpdates() != null) {
            response.setStopTimeUpdates(
                    trip.getStopTimeUpdates().stream()
                            .map(StopTimeUpdateInfo::from)
                            .collect(Collectors.toList())
            );
        }

        return response;
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

    public VehicleInfo getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleInfo vehicle) {
        this.vehicle = vehicle;
    }

    public List<StopTimeUpdateInfo> getStopTimeUpdates() {
        return stopTimeUpdates;
    }

    public void setStopTimeUpdates(List<StopTimeUpdateInfo> stopTimeUpdates) {
        this.stopTimeUpdates = stopTimeUpdates;
    }

    // Nested classes for nested data
    public static class VehicleInfo {
        private String id;
        private String label;

        public static VehicleInfo from(Vehicle vehicle) {
            VehicleInfo info = new VehicleInfo();
            info.setId(vehicle.getId());
            info.setLabel(vehicle.getLabel());
            return info;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    public static class StopTimeUpdateInfo {
        private int stopSequence;
        private String stopId;
        private ArrivalInfo arrival;
        private DepartureInfo departure;

        public static StopTimeUpdateInfo from(StopTimeUpdate stopUpdate) {
            StopTimeUpdateInfo info = new StopTimeUpdateInfo();
            info.setStopSequence(stopUpdate.getStopSequence());
            info.setStopId(stopUpdate.getStopId());

            if (stopUpdate.getArrival() != null) {
                info.setArrival(ArrivalInfo.from(stopUpdate.getArrival()));
            }

            if (stopUpdate.getDeparture() != null) {
                info.setDeparture(DepartureInfo.from(stopUpdate.getDeparture()));
            }

            return info;
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

        public ArrivalInfo getArrival() {
            return arrival;
        }

        public void setArrival(ArrivalInfo arrival) {
            this.arrival = arrival;
        }

        public DepartureInfo getDeparture() {
            return departure;
        }

        public void setDeparture(DepartureInfo departure) {
            this.departure = departure;
        }
    }

    public static class ArrivalInfo {
        private String formattedTime;
        private long unixTime;
        private int delaySeconds;

        public static ArrivalInfo from(com.transittracker.model.StopTimeEvent arrival) {
            ArrivalInfo info = new ArrivalInfo();
            info.setUnixTime(arrival.getTime());
            info.setFormattedTime(DateUtils.formatTimestamp(arrival.getTime()));
            info.setDelaySeconds(arrival.getDelay());
            return info;
        }

        public String getFormattedTime() {
            return formattedTime;
        }

        public void setFormattedTime(String formattedTime) {
            this.formattedTime = formattedTime;
        }

        public long getUnixTime() {
            return unixTime;
        }

        public void setUnixTime(long unixTime) {
            this.unixTime = unixTime;
        }

        public int getDelaySeconds() {
            return delaySeconds;
        }

        public void setDelaySeconds(int delaySeconds) {
            this.delaySeconds = delaySeconds;
        }
    }

    public static class DepartureInfo {
        private String formattedTime;
        private long unixTime;
        private int delaySeconds;

        public static DepartureInfo from(com.transittracker.model.StopTimeEvent departure) {
            DepartureInfo info = new DepartureInfo();
            info.setUnixTime(departure.getTime());
            info.setFormattedTime(DateUtils.formatTimestamp(departure.getTime()));
            info.setDelaySeconds(departure.getDelay());
            return info;
        }

        public String getFormattedTime() {
            return formattedTime;
        }

        public void setFormattedTime(String formattedTime) {
            this.formattedTime = formattedTime;
        }

        public long getUnixTime() {
            return unixTime;
        }

        public void setUnixTime(long unixTime) {
            this.unixTime = unixTime;
        }

        public int getDelaySeconds() {
            return delaySeconds;
        }

        public void setDelaySeconds(int delaySeconds) {
            this.delaySeconds = delaySeconds;
        }
    }
}

