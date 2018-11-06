package com.example.ryu_w.calendar;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewFlipper;

public class MainActivity extends AppCompatActivity {
    EditText farmAddress, crops, area, address, price_per_area, working_content;
    Button btn_save, setting_save;
    SQLiteDatabase sqlDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        farmAddress = (EditText) findViewById(R.id.farm_address);
        crops = (EditText) findViewById(R.id.crops);
        area = (EditText) findViewById(R.id.area);
        address = (EditText) findViewById(R.id.address);
        price_per_area = (EditText) findViewById(R.id.price_per_area);
        working_content = (EditText) findViewById(R.id.working_content);
        btn_save = (Button) findViewById(R.id.button2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu); //
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        ViewFlipper vf = (ViewFlipper) findViewById(R.id.vf);

        if (id == R.id.action_toNotification) {
            Intent intent = new Intent(getApplicationContext(), Notification.class);
            startActivity(intent);
        } else if (id == R.id.action_toCalendar) {
            Intent intent = new Intent(getApplicationContext(), Calendar.class);
            startActivity(intent);
        } else if (id == R.id.action_toDailylog) {
            Intent intent = new Intent(getApplicationContext(), WriteDailyLog.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}
