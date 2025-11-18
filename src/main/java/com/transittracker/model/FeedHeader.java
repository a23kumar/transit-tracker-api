package com.transittracker.model;

/**
 * Domain model representing the GTFS Realtime feed header
 */
public class FeedHeader {
    private String gtfsRealtimeVersion;
    private long timestamp;

    public FeedHeader() {
    }

    public FeedHeader(String gtfsRealtimeVersion, long timestamp) {
        this.gtfsRealtimeVersion = gtfsRealtimeVersion;
        this.timestamp = timestamp;
    }

    public String getGtfsRealtimeVersion() {
        return gtfsRealtimeVersion;
    }

    public void setGtfsRealtimeVersion(String gtfsRealtimeVersion) {
        this.gtfsRealtimeVersion = gtfsRealtimeVersion;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

