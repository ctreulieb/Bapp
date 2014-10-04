package com.ltcnextbus.ct.ltcnextbus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.view.View.OnClickListener;

public class Favourites extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        Spinner favSpinner = (Spinner) findViewById(R.id.favSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.stop_array, android.R.layout.simple_spinner_dropdown_item);
        favSpinner.setAdapter(adapter);

        ((Button)findViewById(R.id.buttonGetNextBusses)).setOnClickListener(this);
        ((Button)findViewById(R.id.buttonRemoveStop)).setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.favourites, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main: {
                Intent myIntent = new Intent(Favourites.this, LTCNextBusMain.class);
                startActivity(myIntent);
            }
            case R.id.about: {
                Intent myIntent = new Intent(Favourites.this, about.class);
                startActivity(myIntent);
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.buttonGetNextBusses :
            case R.id.buttonRemoveStop :
            default:
                return;
        }
    }
}
