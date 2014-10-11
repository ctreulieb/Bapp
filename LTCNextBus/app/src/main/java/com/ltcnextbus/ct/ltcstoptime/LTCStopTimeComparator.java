package com.ltcnextbus.ct.ltcstoptime;

import android.text.format.Time;

import java.util.Comparator;

/**
 * Created by Craig on 11/10/2014.
 */
public class LTCStopTimeComparator implements Comparator<LTCStopTime> {
    @Override
    public int compare(LTCStopTime ltcStopTime, LTCStopTime ltcStopTime2) {
        return Time.compare(ltcStopTime.getTime(), ltcStopTime2.getTime());
    }
}
