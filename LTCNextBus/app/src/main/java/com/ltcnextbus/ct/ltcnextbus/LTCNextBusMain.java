package com.ltcnextbus.ct.ltcnextbus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.view.View.OnClickListener;

import com.ltcnextbus.ct.ltcscraper.LTCScraper;
import com.ltcnextbus.ct.ltcscraper.LTCStopTime;

import java.util.ArrayList;

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
            case R.id.buttonGetNextBusses :
            default:
                return;
        }
    }

    public void getNextBusesClick(View view) {
        ArrayList<LTCStopTime> stopTimes = LTCScraper.getTimesForStop(39,this);
    }
}
