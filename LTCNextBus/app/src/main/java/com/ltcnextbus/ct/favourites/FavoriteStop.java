package com.ltcnextbus.ct.favourites;

import java.io.Serializable;

/**
 * Created by Tyler on 04/10/2014.
 */
public class FavoriteStop implements Serializable {
    private int stopID;
    private String name;

    public FavoriteStop(int id, String name) {
        this.stopID = id;
        this.name = name;
    }

    public int getStopID() {
        return stopID;
    }

    public void setStopID(int stopID) {
        this.stopID = stopID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
