package com.example.ryu_w.calendar;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewFlipper;

public class Farm_Setting extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    EditText farmAddress, crops, area, address, price_per_area, working_content;
    Button btn_save, setting_save;
    SQLiteDatabase sqlDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        // (1) activity_index에서 include된 Acitivity 띄우기
        ViewFlipper viewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper);
        viewFlipper.setDisplayedChild(1); // ~ (1)

        // (2) Navigation_drawer Activity에 필요한 메뉴바 띄우기
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this); // ~ (2)

        farmAddress = (EditText) findViewById(R.id.farm_address);
        crops = (EditText) findViewById(R.id.crops);
        area = (EditText) findViewById(R.id.area);
        address = (EditText) findViewById(R.id.address);
        price_per_area = (EditText) findViewById(R.id.price_per_area);
        working_content = (EditText) findViewById(R.id.working_content);
        btn_save = (Button) findViewById(R.id.button2);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_temp, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_setting) {
            Intent intent = new Intent(getApplicationContext(), Farm_Setting.class);
            startActivity(intent);
        } else if (id == R.id.nav_notification) {
            Intent intent = new Intent(getApplicationContext(), Notification.class);
            startActivity(intent);
        } else if (id == R.id.nav_calendar) {
            Intent intent = new Intent(getApplicationContext(), Calendar.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu); //
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        ViewFlipper vf = (ViewFlipper) findViewById(R.id.vf);
//
//        if (id == R.id.action_toNotification) {
//            Intent intent = new Intent(getApplicationContext(), Notification.class);
//            startActivity(intent);
//        } else if (id == R.id.action_toCalendar) {
//            Intent intent = new Intent(getApplicationContext(), Calendar.class);
//            startActivity(intent);
//        } else if (id == R.id.action_toDailylog) {
//            Intent intent = new Intent(getApplicationContext(), WriteDailyLog.class);
//            startActivity(intent);
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


}
