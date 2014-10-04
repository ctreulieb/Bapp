package com.ltcnextbus.ct.ltcnextbus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class about extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main: {
                Intent myIntent = new Intent(about.this, LTCNextBusMain.class);
                startActivity(myIntent);
            }
            case R.id.favorites: {
                Intent myIntent = new Intent(about.this, Favourites.class);
                startActivity(myIntent);
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
