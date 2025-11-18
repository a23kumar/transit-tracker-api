package com.transittracker.model;

/**
 * Domain model representing a stop time event (arrival or departure)
 */
public class StopTimeEvent {
    private long time; // Unix timestamp in seconds
    private int delay; // Delay in seconds

    public StopTimeEvent() {
    }

    public StopTimeEvent(long time, int delay) {
        this.time = time;
        this.delay = delay;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
}

