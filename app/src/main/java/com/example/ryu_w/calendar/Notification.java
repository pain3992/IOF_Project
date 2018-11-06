package com.example.ryu_w.calendar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

public class Notification extends AppCompatActivity {
    EditText H_temp,L_temp,H_humi,L_humi,H_CO2,L_CO2,H_rootT,L_rootT,H_soil,L_soil;
    Button setting_save;

    DynamoDBMapper dynamoDBMapper;   // 1106 미팅

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        ViewFlipper vf = (ViewFlipper)findViewById(R.id.vf);
        vf.setDisplayedChild(1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        ViewFlipper vf = (ViewFlipper) findViewById(R.id.vf);

        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
}
