package com.ltcnextbus.ct.ltcscraper;

import android.text.format.Time;

/**
 * Created by Craig on 07/10/2014.
 */
public class LTCStopTime {
    private int routeID;
    private String routeName;
    private Time time;

    public int getRouteID() {
        return routeID;
    }

    public void setRouteID(int routeID) {
        this.routeID = routeID;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }
}
