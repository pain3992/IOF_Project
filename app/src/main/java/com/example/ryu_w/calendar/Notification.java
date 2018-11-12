package com.example.ryu_w.calendar;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

public class Notification extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    EditText H_temp,L_temp,H_humi,L_humi,H_CO2,L_CO2,H_rootT,L_rootT,H_soil,L_soil;
    Button setting_save;

    DynamoDBMapper dynamoDBMapper;   // 1106 미팅

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        // (1) activity_index에서 include된 Acitivity 띄우기
        ViewFlipper viewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper);
        viewFlipper.setDisplayedChild(2);

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


        // AWSMobileClient enables AWS user credentials to access your table
        AWSMobileClient.getInstance().initialize(this).execute(); // USED

        AWSCredentialsProvider credentialsProvider = AWSMobileClient.getInstance().getCredentialsProvider(); // USED
        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration(); // USED


        // Add code to instantiate a AmazonDynamoDBClient
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(credentialsProvider); // USED

        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(configuration)
                .build();
        // other activity code ...

        H_temp = (EditText) findViewById(R.id.H_temp);
        L_temp = (EditText) findViewById(R.id.L_temp);
        H_humi = (EditText) findViewById(R.id.H_humi);
        L_humi = (EditText) findViewById(R.id.L_humi);
        H_CO2 = (EditText) findViewById(R.id.H_CO2);
        L_CO2 = (EditText) findViewById(R.id.L_CO2);
        H_rootT = (EditText) findViewById(R.id.H_rootT);
        L_rootT = (EditText) findViewById(R.id.L_rootT);
        H_soil = (EditText) findViewById(R.id.H_soil);
        L_soil = (EditText) findViewById(R.id.L_soil);

        setting_save = findViewById(R.id.setting_save);
        setting_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Runnable runnable = new Runnable() { //DDB 비동기 호출
                    public void run() {
                        createNotification();
                    }
                };
                Thread mythread = new Thread(runnable);
                mythread.start();

                Toast.makeText(getApplicationContext(), "설정이 저장 되었습니다.", Toast.LENGTH_LONG).show();
            }
        });
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
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
//        if (id == R.id.action_settings) {
//            Intent intent = new Intent(getApplicationContext(), Farm_Setting.class);
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

    public void createNotification() {
        final com.example.ryu_w.calendar.notificationDO notificationItem = new  com.example.ryu_w.calendar.notificationDO();

        notificationItem.setNId("sensor_p1"); //sensor_p1 고정
        notificationItem.setNTime("2018-10-12T"); //저장일시를 받아올 것

        //온도
        notificationItem.setNtemp(H_temp.getText().toString());
        notificationItem.setNLtemp(L_temp.getText().toString());

        //습도
        notificationItem.setNhumi(H_humi.getText().toString());
        notificationItem.setNLhumi(L_humi.getText().toString());

        //CO2
        notificationItem.setNCO2(H_CO2.getText().toString());
        notificationItem.setNLCO2(L_CO2.getText().toString());

        //뿌리온도
        notificationItem.setNrootT(H_rootT.getText().toString());
        notificationItem.setNLrootT(L_rootT.getText().toString());

        //조도
        notificationItem.setNsoil(H_soil.getText().toString());
        notificationItem.setNLsoil(L_soil.getText().toString());

        new Thread(new Runnable() {
            @Override
            public void run() {
                dynamoDBMapper.save(notificationItem);
                // Item saved

            }
        }).start();
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
}

