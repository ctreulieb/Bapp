package com.ltcnextbus.ct.ltcnextbus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.view.View.OnClickListener;

import com.ltcnextbus.ct.favourites.FavouriteFileManager;
import com.ltcnextbus.ct.favourites.FavouriteStop;

import java.util.ArrayList;

/**
 * Created by Tyler on 04/10/2014.
 *
 *  Activity for the Favourites page. Handles all the actions a user can do to a favourite stop
 */


public class Favourites extends Activity implements OnClickListener {

    private ArrayList<FavouriteStop> favStops;
    private FavouriteFileManager fileManager = new FavouriteFileManager(this);
    private int selectedFavIndex = 0;
    private Spinner favSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);


        ((Button)findViewById(R.id.buttonGetNextBuses)).setOnClickListener(this);
        ((Button)findViewById(R.id.buttonRemoveStop)).setOnClickListener(this);

        favStops = fileManager.readFromInternalStorage();
        if(null == favStops) {
            favStops = new ArrayList<FavouriteStop>();
        }
        initSpinner();
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
            case R.id.main: {
                Intent myIntent = new Intent(Favourites.this, LTCNextBusMain.class);
                startActivity(myIntent);
                return true;
            }
            case R.id.about: {
                Intent myIntent = new Intent(Favourites.this, About.class);
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
            case R.id.buttonGetNextBuses :
                if(favStops.size() != 0) {
                    Intent i = new Intent(Favourites.this, LTCNextBusMain.class);
                    i.putExtra("stopID", favStops.get(selectedFavIndex).getStopID());
                    startActivity(i);
                }
                break;
            case R.id.buttonRemoveStop :
                if(favStops.size() != 0) {
                    favStops.remove(selectedFavIndex);
                    initSpinner();
                    selectedFavIndex = favSpinner.getSelectedItemPosition();
                    fileManager.saveFavouritesToFile(favStops);
                }
                break;
            default:
                return;
        }
    }

    private void initSpinner() {
        String[] names = new String[favStops.size()];

        for(int i = 0; i < names.length; ++i) {
            names[i] = favStops.get(i).getName();
        }

        favSpinner = (Spinner) findViewById(R.id.favSpinner);
        ArrayAdapter<String> adapter =  new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, names);
        favSpinner.setAdapter(adapter);
        favSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                selectedFavIndex = arg2;

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                selectedFavIndex = 0;
            }
        });
        selectedFavIndex = favSpinner.getSelectedItemPosition();
    }

}
