package com.ltcnextbus.ct.ltcstoptime;

import android.text.format.Time;

/**
 * Created by Craig on 07/10/2014.
 * LTCStopTime Class to contain all relevant information about a single stop time
 */
public class LTCStopTime {
    private int routeID;
    private String destination;
    private Time time;

    public int getRouteID() {
        return routeID;
    }

    public void setRouteID(int routeID) {
        this.routeID = routeID;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }
}