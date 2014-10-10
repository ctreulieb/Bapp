package com.ltcnextbus.ct.ltcnextbus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.view.View.OnClickListener;
import android.text.format.Time;

import com.ltcnextbus.ct.favourites.FavoriteFileManager;
import com.ltcnextbus.ct.favourites.FavoriteStop;
import com.ltcnextbus.ct.ltcstoptime.LTCStopTime;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LTCNextBusMain extends Activity implements OnClickListener {

    private FavoriteFileManager fileManager = new FavoriteFileManager(this);
    private ListView listView;
    private Button favButton;
    private EditText stopIDEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ltcnext_bus_main);

        Bundle b = getIntent().getExtras();
        if(null != b) {
            int stopID = b.getInt("stopID");
            new scrapeAsync(stopID).execute();
        }

        listView = (ListView) findViewById(R.id.listView);

        String[] values = new String[] { "#17 5:50", "#4a 6:00", "#17 6:20", "#4a 6:30", "#17 6:50", "#4a 7:00", "#17 7:20", "#4a 7:30",  "#17 7:50", "#4a 8:00", };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);

        listView.setAdapter(adapter);

        ((Button)findViewById(R.id.buttonAddToFav)).setOnClickListener(this);
        ((Button)findViewById(R.id.buttonGetNextBuses)).setOnClickListener(this);
        stopIDEditText = (EditText)findViewById(R.id.editTextStopNumber);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ltcnext_bus_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.favorites: {
                Intent myIntent = new Intent(LTCNextBusMain.this, Favourites.class);
                startActivity(myIntent);
                return true;
            }
            case R.id.about: {
                Intent myIntent = new Intent(LTCNextBusMain.this, About.class);
                startActivity(myIntent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.buttonAddToFav :
                buildAndExecFavAlert();
                break;
            case R.id.buttonGetNextBuses :
                new scrapeAsync(2504).execute();
                break;
            default:
                return;
        }
    }

    private class scrapeAsync extends AsyncTask<Void, Void, Void> {
        private List<Integer> routes;
        private int stopID;
        ArrayList<LTCStopTime> stopTimes = new ArrayList<LTCStopTime>();
        //TODO: HANDLE IF THE STOP ID DOES NOT EXIST
        //TODO: INFORM THE USER IT IS GETTING STOP TIMES ETC. "onProgressUpdate"?
        //TODO: HANDLE NO MORE STOP TIMES
        //TODO: Handle NO NETWORK CONNECTION

        public scrapeAsync(int stopID)
        {
            this.stopID = stopID;
            XmlResourceParser stops = getApplicationContext().getResources().getXml(R.xml.ltcstops);
            routes = new ArrayList<Integer>();
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
        }


        @Override
        protected Void doInBackground(Void... params) {
            try{
                for(int i : routes){
                    for(int d = 1; d <5; ++d) {
                        Document doc = Jsoup.connect("http://ltconline.ca/WebWatch/ada.aspx?r=" + i + "&d=" + d + "&s=" + stopID).get();
                        Elements stopResults = doc.select("#tblADA TR");
                        if(stopResults.size()> 1)
                        {
                            stopResults.remove(stopResults.size()-1);
                            stopResults.remove(0);
                            for(Element s : stopResults){
                                String time = s.child(0).text();
                                int hr = Integer.parseInt(time.substring(1, time.indexOf(":")));
                                int mn = Integer.parseInt(time.substring(time.indexOf(":")+1, time.lastIndexOf(" ")));
                                if(time.indexOf("P") > 0)
                                    hr += 12;
                                LTCStopTime st = new LTCStopTime();
                                st.setDestination(s.child(1).text());
                                st.setRouteID(i);
                                Time t = new Time();
                                t.set(0,mn,hr,0,0,0);
                                st.setTime(t);
                                int bp =0;
                                bp += 5;
                                stopTimes.add(st);
                            }
                            break;
                        }
                    }
                }
            }catch (IOException e) {
                System.out.println("IOException - " + e.getMessage());
            }
            return null;
        }
    }
    private boolean isStop(int stopID) {
        XmlResourceParser stops = getApplicationContext().getResources().getXml(R.xml.ltcstops);
        try {
            stops.next();
            int eventType = stops.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_TAG && stops.getName().equalsIgnoreCase("stop") && stops.getAttributeIntValue(null, "number", 0) == stopID)
                    return true;
                eventType = stops.next();
            }
        }catch (XmlPullParserException e) {
            System.out.println("XMLPullParserException - " + e.getMessage());
        }catch (IOException e) {
            System.out.println("IOException - " + e.getMessage());
        }
        return false;
    }

    private String getStopName(int stopID) {
        String stopName = "";
        XmlResourceParser stops = getApplicationContext().getResources().getXml(R.xml.ltcstops);
        try {
            stops.next();
            int eventType = stops.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_TAG && stops.getName().equalsIgnoreCase("stop") && stops.getAttributeIntValue(null, "number", 0) == stopID) {
                    return stops.getAttributeValue(null,"name");
                }
                eventType = stops.next();
            }
        }catch (XmlPullParserException e) {
            System.out.println("XMLPullParserException - " + e.getMessage());
        }catch (IOException e) {
            System.out.println("IOException - " + e.getMessage());
        }
        return stopName;
    }

    private void buildAndExecFavAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Add To Favourites");
        alert.setMessage("Give The Stop A Name");
        //TODO add in handles for bad input and not a stop
        String stopName = "";
        final int stopNumber;
        if("" == stopIDEditText.getText().toString()) {
            return;
        } else {
            stopNumber =  Integer.parseInt(stopIDEditText.getText().toString());
            if(isStop(stopNumber)) {
                stopName =  getStopName(stopNumber);
            }
        }

        final EditText input = new EditText(this);
        input.setText(stopName);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String value = input.getText().toString();
                ArrayList<FavoriteStop> favStops;
                favStops = fileManager.readFromInternalStorage();
                if(null == favStops) {
                    favStops = new ArrayList<FavoriteStop>();
                }

                for(int iFavs = 0; iFavs < favStops.size(); ++iFavs) {
                    if(favStops.get(iFavs).getStopID() == stopNumber) {
                        //TODO Handle id already in favs
                        return;
                    }
                }
                favStops.add(new FavoriteStop(stopNumber, value));
                fileManager.saveFavoritesToFile(favStops);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.show();
    }
}
