package com.ltcnextbus.ct.ltcscraper;

import android.app.Application;
import android.content.Context;
import android.content.res.XmlResourceParser;

import com.ltcnextbus.ct.ltcnextbus.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Craig on 07/10/2014.
 */
public class LTCScraper {
    public static ArrayList<LTCStopTime> getTimesForStop(int stopID, Context context) {
        ArrayList<LTCStopTime> stopTimes = new ArrayList<LTCStopTime>();
        XmlResourceParser stops = context.getResources().getXml(R.xml.ltcstops);
        List<Integer> routes = new ArrayList<Integer>();
        try {
            stops.next();
            int eventType = stops.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_TAG && stops.getName().equalsIgnoreCase("stop") && stops.getAttributeIntValue(null, "number", 0) == stopID){
                    //found <stop> now need to find all the routes
                    //go until you find <routes> start tag then until it isn't routes end tag if it's
                    while(!(eventType == XmlPullParser.END_TAG && stops.getName().equalsIgnoreCase("routes"))) {
                        if(eventType == XmlPullParser.START_TAG && stops.getName().equalsIgnoreCase("route")) {
                            routes.add(Integer.parseInt(stops.nextText()));
                        }
                        eventType = stops.next();
                    }
                    break;
                }
                eventType = stops.next();
            }
        }catch (XmlPullParserException e) {
            System.out.println("XMLPullParserException - " + e.getMessage());
        }catch (IOException e) {
            System.out.println("IOException - " + e.getMessage());
        }
        int bp = 000;

        return stopTimes;
    }

}
