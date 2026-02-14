package com.transittracker.model;

public class StopTimeEvent {
    private long time;
    private int delay;

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
