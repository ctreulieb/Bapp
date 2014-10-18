package com.ltcnextbus.ct.ltcnextbus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.view.View.OnClickListener;
import android.text.format.Time;

import com.ltcnextbus.ct.favourites.FavoriteFileManager;
import com.ltcnextbus.ct.favourites.FavoriteStop;
import com.ltcnextbus.ct.ltcstoptime.LTCStopTime;
import com.ltcnextbus.ct.ltcstoptime.LTCStopTimeComparator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

        listView = (ListView) findViewById(R.id.listView);
        ((Button)findViewById(R.id.buttonAddToFav)).setOnClickListener(this);
        ((Button)findViewById(R.id.buttonGetNextBuses)).setOnClickListener(this);
        stopIDEditText = (EditText)findViewById(R.id.editTextStopNumber);

        Bundle b = getIntent().getExtras();
        if(null != b) {
            int stopID = b.getInt("stopID");
            stopIDEditText.setText("" + stopID);
            new scrapeAsync(this, stopID).execute();
        }
    }

    private void setListView(String[] values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);
        listView.setAdapter(adapter);
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
                String value = stopIDEditText.getText().toString();
                if(tryParseInt(value)) {
                    int stop = Integer.parseInt(value);
                    if(isStop(stop)) {
                        if(haveNetworkConnection())
                            new scrapeAsync(this, stop).execute();
                        else {
                            Toast.makeText(getApplicationContext(),"Network connection required to fetch stop times",Toast.LENGTH_LONG).show();
                            return;
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"Not a valid stop number",Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    displayNotValidInputToast();
                    return;
                }
                break;
            default:
                return;
        }
    }

    private class scrapeAsync extends AsyncTask<Void, Void,  ArrayList<LTCStopTime>> {
        private List<Integer> routes;
        private int stopID;
        ArrayList<LTCStopTime> stopTimes = new ArrayList<LTCStopTime>();
        private ProgressDialog progress;
        private Context context;
        //TODO: INFORM THE USER IT IS GETTING STOP TIMES ETC. "onProgressUpdate"?

        public scrapeAsync(Context c ,int stopID)
        {
            this.context = c;
            this.stopID = stopID;
            XmlResourceParser stops = getApplicationContext().getResources().getXml(R.xml.ltcstops);
            routes = new ArrayList<Integer>();
            try {
                stops.next();
                int eventType = stops.getEventType();
                while(eventType != XmlPullParser.END_DOCUMENT) {
                    if(eventType == XmlPullParser.START_TAG && stops.getName().equalsIgnoreCase("stop") && stops.getAttributeIntValue(null, "number", 0) == stopID){
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
        protected void onPreExecute() {
            progress = new ProgressDialog(context);
            progress.setTitle("Loading");
            progress.setMessage("Fetching Stop Times");
            progress.show();
        }

        @Override
        protected  ArrayList<LTCStopTime> doInBackground(Void... params) {
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
                Collections.sort(stopTimes, new LTCStopTimeComparator());
            }catch (IOException e) {
                System.out.println("IOException - " + e.getMessage());
            }
            return stopTimes;
        }

        @Override
        protected void onPostExecute(ArrayList<LTCStopTime> result) {
            DateFormat df = new DateFormat();
            String[] values;
            if(result.size() > 0) {
                values = new String[result.size()];
                for(int i = 0; i < result.size(); ++i) {
                    values[i] = "" + result.get(i).getTime().format("%H:%M") + " - Route #" + result.get(i).getRouteID() + " " + result.get(i).getDestination();
                }
            } else{
                values = new String[1];
                values[0] = "No more stop times for this stop";
            }

            setListView(values);
            progress.dismiss();
         }
    }

    private void displayNotValidInputToast() {
        Toast.makeText(getApplicationContext(),"Invalid Input",Toast.LENGTH_SHORT).show();
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

    private boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        }catch(NumberFormatException nfe) {
            return false;
        }
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private void buildAndExecFavAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Add To Favourites");
        alert.setMessage("Give this stop a name");
        String stopName = "";
        final int stopNumber;
        if(stopIDEditText.getText().toString().equals("")) {
            return;
        } else {
            if(tryParseInt(stopIDEditText.getText().toString())) {
                stopNumber =  Integer.parseInt(stopIDEditText.getText().toString());
                if(isStop(stopNumber)) {
                    stopName =  getStopName(stopNumber);
                } else {
                    Toast.makeText(getApplicationContext(),"Not A Stop Number",Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                displayNotValidInputToast();
                return;
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
                        Toast.makeText(getApplicationContext(),"Stop Number Already A Favourite",Toast.LENGTH_SHORT).show();
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
