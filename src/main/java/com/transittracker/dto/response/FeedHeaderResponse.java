package com.transittracker.dto.response;

import com.transittracker.model.FeedHeader;

/**
 * DTO for feed header response
 */
public class FeedHeaderResponse {
    private String gtfsRealtimeVersion;
    private long timestamp;
    private String formattedTimestamp;

    public FeedHeaderResponse() {
    }

    public static FeedHeaderResponse from(FeedHeader header) {
        FeedHeaderResponse response = new FeedHeaderResponse();
        response.setGtfsRealtimeVersion(header.getGtfsRealtimeVersion());
        response.setTimestamp(header.getTimestamp());
        response.setFormattedTimestamp(com.transittracker.util.DateUtils.formatTimestamp(header.getTimestamp()));
        return response;
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

    public String getFormattedTimestamp() {
        return formattedTimestamp;
    }

    public void setFormattedTimestamp(String formattedTimestamp) {
        this.formattedTimestamp = formattedTimestamp;
    }
}

