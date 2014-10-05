package com.ltcnextbus.ct.favourites;

import java.io.Serializable;

/**
 * Created by Tyler on 04/10/2014.
 */
public class FavoriteStop implements Serializable {
    private String stopID;
    private String name;

    public String getStopID() {
        return stopID;
    }

    public void setStopID(String stopID) {
        this.stopID = stopID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
