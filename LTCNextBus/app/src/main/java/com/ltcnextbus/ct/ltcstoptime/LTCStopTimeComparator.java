package com.ltcnextbus.ct.ltcstoptime;

import android.text.format.Time;

import java.util.Comparator;

/**
 * Created by Craig on 11/10/2014.
 * Allows for sorting a list of LTCStopTime objects, sorts them by time
 */
public class LTCStopTimeComparator implements Comparator<LTCStopTime> {
    @Override
    public int compare(LTCStopTime ltcStopTime, LTCStopTime ltcStopTime2) {
        return Time.compare(ltcStopTime.getTime(), ltcStopTime2.getTime());
    }
}
