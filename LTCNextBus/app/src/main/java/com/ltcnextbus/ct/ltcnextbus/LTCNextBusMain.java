package com.ltcnextbus.ct.ltcnextbus;

import android.app.Activity;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.view.View.OnClickListener;

import com.ltcnextbus.ct.ltcstoptime.LTCStopTime;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LTCNextBusMain extends Activity implements OnClickListener {


    private ListView listView;
    private Button favButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ltcnext_bus_main);

        Bundle b = getIntent().getExtras();
        if(null != b) {
            int stopID = b.getInt("stopID");
            //TODO auto populate with this stop id
        }

        listView = (ListView) findViewById(R.id.listView);

        String[] values = new String[] { "#17 5:50", "#4a 6:00", "#17 6:20", "#4a 6:30", "#17 6:50", "#4a 7:00", "#17 7:20", "#4a 7:30",  "#17 7:50", "#4a 8:00", };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);

        listView.setAdapter(adapter);

        ((Button)findViewById(R.id.buttonAddToFav)).setOnClickListener(this);
        //((Button)findViewById(R.id.buttonGetNextBusses)).setOnClickListener(this);
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
            case R.id.buttonGetNextBuses :
            default:
                return;
        }
    }

    public void getNextBusesClick(View view) {
        new scrapeAsync(39).execute();

    }

    private class scrapeAsync extends AsyncTask<Void, Void, Void> {
        private List<Integer> routes;
        private int stopID;
        ArrayList<LTCStopTime> stopTimes = new ArrayList<LTCStopTime>();

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
                        if(stopResults.size() > 1)
                        {
                            //get stop info
                        }
                    }
                }
            }catch (IOException e) {
                System.out.println("IOException - " + e.getMessage());
            }
            return null;
        }
    }
}
