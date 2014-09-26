package com.ltcnextbus.ct.ltcnextbus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.*;
import java.util.ArrayList;
import android.view.View.OnClickListener;

public class LTCNextBusMain extends Activity {


    private ListView listView;
    private Button favButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ltcnext_bus_main);

        listView = (ListView) findViewById(R.id.listView);

        String[] values = new String[] { "#17 5:50", "#4a 6:00", "#17 6:20", "#4a 6:30", "#17 6:50", "#4a 7:00", "#17 7:20", "#4a 7:30",  "#17 7:50", "#4a 8:00", };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);

        listView.setAdapter(adapter);

        favButton = (Button) findViewById(R.id.button2);

        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LTCNextBusMain.this, Favourites.class);
                startActivity(myIntent);
            }
        };
        favButton.setOnClickListener(onClickListener);

        //Intent myIntent = new Intent(this, AvitivityName.class);
       // startActivity(myIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ltcnext_bus_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
