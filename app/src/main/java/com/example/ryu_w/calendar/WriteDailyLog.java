package com.example.ryu_w.calendar;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;


import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.google.gson.Gson;

public class WriteDailyLog extends AppCompatActivity {

    // 변수 선언
    EditText edt_date; // 날짜 받아옴
    TextView tv_sensor; // 센서 TextView
    float battery, channel, temp, rootT, humid, co2, soil; // DB에 넣을 자료형 변환
    DBHelper myHelper; // DB연결
    SQLiteDatabase sqlDB;

    Button btn_SendData, awsLoad; //정보 전송, 업데이트 버튼
    EditText edt_BatteryText, edt_ChannelText, edt_ContentText, edt_TempText, edt_HumidText, edt_RootTText, edt_Co2Text, edt_SoilTText;
    DynamoDBMapper dynamoDBMapper; //사용해야할 코드(1-1)!!

    DatePickerDialog.OnDateSetListener listener;

    Intent inIntent, outIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dailylog);

//        ViewFlipper vf = (ViewFlipper)findViewById(R.id.vf);
//        vf.setDisplayedChild(3);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        edt_BatteryText = (EditText) findViewById(R.id.battery_text);
        edt_ChannelText = (EditText) findViewById(R.id.channel_text);
        edt_ContentText = (EditText) findViewById(R.id.dailylogContent);
        edt_TempText = (EditText) findViewById(R.id.temp_text);
        edt_HumidText = (EditText) findViewById(R.id.humid_text);
        edt_RootTText = (EditText) findViewById(R.id.rootT_text);
        edt_Co2Text = (EditText) findViewById(R.id.co2_text);
        edt_SoilTText = (EditText) findViewById(R.id.soilT_text);
        edt_date = (EditText) findViewById(R.id.et_date);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        final int cYear = calendar.get(java.util.Calendar.YEAR);
        final int cMonth = calendar.get(java.util.Calendar.MONTH);
        final int cDay = calendar.get(java.util.Calendar.DAY_OF_MONTH);

        // sensor_p1 텍스트 출력
        tv_sensor = (TextView) findViewById(R.id.tv_sensor);
        SpannableStringBuilder builder = new SpannableStringBuilder("sensor_p1");
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_sensor.append(builder);

        // Intent로 넘긴 날짜 값 저장
        inIntent = getIntent(); //Main에서 넘긴 날짜 값 받아옴
        String receive_date = inIntent.getStringExtra("send_date");
        if (TextUtils.isEmpty(receive_date)) { // 선택한 날짜 없으면 오늘 날짜 저장
            receive_date = cYear + "-" +
                    (cMonth > 8 ? (cMonth + 1) : "0" + (cMonth + 1)) + "-" +
                    (cDay > 9 ? cDay : "0" + cDay);
        }

        // 받아온 날짜를 EditText에 입력
        edt_date.setText(receive_date);
        edt_date.setInputType(0);

        // AWS 연결.  AWSMobileClient를 사용하면 AWS 사용자 자격 증명을 사용하여 테이블에 액세스 할 수 있습니다.
        AWSMobileClient.getInstance().initialize(this).execute();

        AWSCredentialsProvider credentialsProvider = AWSMobileClient.getInstance().getCredentialsProvider();
        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();

        // AmazonDynamoDBClient를 인스턴스화하는 코드 추가
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(credentialsProvider);

        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(configuration)
                .build();

        awsLoad = (Button) findViewById(R.id.aws_load);
        Runnable runnable = new Runnable() { // DDB 비동기 호출
            public void run() {
            queryBook();
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();

        // 업데이트 버튼 클릭 시
        awsLoad.setOnClickListener(new View.OnClickListener() { // 버튼을 눌러 쿼리를 수행하는 부분!!
            @Override
            public void onClick(View view) {  // 비동기 호출
                awsLoad.setText(" ");
                RotateAnimation anim = new RotateAnimation(0, 720, 58.5f, 58.5f); // 빙글빙글 애니메이션.
                anim.setDuration(2000); //2초간 지속.
                awsLoad.startAnimation(anim);

                Runnable runnable = new Runnable() { //DDB 비동기 호출
                    public void run() {
                        queryBook();
                    }
                };
                Thread mythread = new Thread(runnable);
                mythread.start();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        awsLoad.setText("새로\n고침");
                    }
                }, 2000);
            }
        });

        // 업데이트 버튼 클릭 시
        awsLoad.setOnClickListener(new View.OnClickListener() { // 버튼을 눌러 쿼리를 수행하는 부분!!
            @Override
            public void onClick(View view) {  // 비동기 호출
                awsLoad.setText(" ");
                RotateAnimation anim = new RotateAnimation(0, 720, 58.5f, 58.5f); // 빙글빙글 애니메이션.
                anim.setDuration(2000); //2초간 지속.
                awsLoad.startAnimation(anim);

                Runnable runnable = new Runnable() { //DDB 비동기 호출
                    public void run() {
                        queryBook();
                    }
                };
                Thread mythread = new Thread(runnable);
                mythread.start();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        awsLoad.setText("새로\n고침");
                    }
                }, 2000);
            }
        });

        // 정보 전송 버튼 클릭
        btn_SendData = (Button) findViewById(R.id.btn_sendData);
        btn_SendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = edt_date.getText().toString();

                // editText -> String형 변환
                String s_battery = edt_BatteryText.getText().toString();
                String s_channel = edt_ChannelText.getText().toString();
                String s_temp = edt_TempText.getText().toString();
                String s_rootT = edt_RootTText.getText().toString();
                String s_humid = edt_HumidText.getText().toString();
                String s_co2 = edt_Co2Text.getText().toString();
                String s_soil = edt_SoilTText.getText().toString();
                try {
                    //long 형으로 변환
                    battery = Float.parseFloat(s_battery);
                    channel = Float.parseFloat(s_channel);
                    temp = Float.parseFloat(s_temp);
                    rootT = Float.parseFloat(s_rootT);
                    humid = Float.parseFloat(s_humid);
                    co2 = Float.parseFloat(s_co2);
                    soil = Float.parseFloat(s_soil);

                    String dbName = "TKLabsDB";
                    String databasePath = getFilesDir().getPath() + "/" + dbName;
                    myHelper = new DBHelper(WriteDailyLog.this, databasePath, null);
                    sqlDB = myHelper.getWritableDatabase();
                    sqlDB.execSQL("INSERT INTO plantTable VALUES ('" + date + "', " + battery + "," + channel + "," + temp + ", " + rootT + ", " + humid + ", " + co2 + "," + soil + ");");
                    sqlDB.close();




                    outIntent = new Intent(getApplicationContext(), Calendar.class);
                    setResult(RESULT_OK, outIntent);
                    finish(); //일지작성 레이아웃 닫음
                    // startActivity(outIntent); // 메인 액티비티 재실행
                    Toast.makeText(getApplicationContext(), "입력되었습니다.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "모든 값을 입력 해 주세요", Toast.LENGTH_SHORT).show();
                }


                try {
                    Runnable runnable = new Runnable() { //DDB 비동기 호출
                        public void run() {
                            createDailyLog();
                        }
                    };
                    Thread mythread = new Thread(runnable);
                    mythread.start();

                    Toast.makeText(getApplicationContext(), "다이나모 DB 입력 완료", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "다이나모 DB 입력 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 날짜 EditText 클릭 시 DateSet 달력 실행
        edt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener mDateSetListner = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        String date_selected = String.valueOf(year) + "-" +
                                String.valueOf(month > 8 ? (month + 1) : "0" + (month + 1)) + "-" +
                                String.valueOf(day > 9 ? day : "0" + day);
                        edt_date.setText(date_selected);
                    }
                };
                DatePickerDialog dialog = new DatePickerDialog(WriteDailyLog.this, mDateSetListner, cYear, cMonth, cDay);
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis()); // 오늘 날짜까지만 선택 가능하도록.
                dialog.show();
            }
        });
    }

    public void queryBook() {// 사용해야하는 쿼리 부분(3)!!

        new Thread(new Runnable() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }

            @Override
            public void run() {
                com.example.ryu_w.calendar.BooksDO book = new com.example.ryu_w.calendar.BooksDO(); //패키지 안의 클래스를 불러오는 과정 (경로 변경시키기)
                //book.setId(edtNum.getText().toString());       //partition key (테이블에서 'dev_id'가 해당되는 부분입니다)
                book.setId("sensor_p1");                       // TODO : 일단 고정 시켜놨어요.
                book.setTime(edt_date.getText().toString()); //range key (테이블에서 'time'이 해당되는 부분입니다)


                Condition rangeKeyCondition = new Condition()
                        .withComparisonOperator(ComparisonOperator.BEGINS_WITH)
                        .withAttributeValueList(new AttributeValue().withS(edt_date.getText().toString()));
                DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression()
                        .withHashKeyValues(book)
                        .withRangeKeyCondition("time", rangeKeyCondition) //('rang key'와 동일한 값 입력)
                        .withConsistentRead(false);

                final PaginatedList<BooksDO> result = dynamoDBMapper.query(com.example.ryu_w.calendar.BooksDO.class, queryExpression);

                Gson gson = new Gson();
                final StringBuilder stringBuilder = new StringBuilder();

                // Loop through query results
                int i = 0;
                try {
                    i = result.size()-1;
                    String jsonFormOfItem = gson.toJson(result.get(i)); //java->gson (자세한 설명은 구글링)
                    stringBuilder.append(jsonFormOfItem + "\n\n");
                    BooksDO B = gson.fromJson(jsonFormOfItem, BooksDO.class); //gson->java (자세한 설명은 구글링)

                    edt_BatteryText.setText(String.valueOf(B.getbat()));
                    edt_ChannelText.setText(String.valueOf(B.getchannel()));
                    edt_TempText.setText(String.valueOf(B.gettemp()));
                    edt_HumidText.setText(String.valueOf(B.gethumi()));
                    edt_RootTText.setText(String.valueOf(B.getrootT()));
                    edt_Co2Text.setText(String.valueOf(B.getCO2()));
                    edt_SoilTText.setText(String.valueOf(B.getsoil()));
                } catch (Exception e) {//결과 값이 빈 값일 때 에러 처리.
                    runOnUiThread(new Runnable() { // 현재 영역이 UI밖이라서 UI에 띄우도록 지정
                        public void run() {
                            Toast toast = Toast.makeText(getApplicationContext(), "값이 없습니다.", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    });
                }
            }
        }).start();
    }

    // 뒤로가기 버튼 : 출처 : http://ccdev.tistory.com/12
    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);     // 여기서 this는 Activity의 this

        // 여기서 부터는 알림창의 속성 설정
        builder.setMessage("일지 작성을 취소 하시겠습니까?")        // 메세지 설정
                .setCancelable(true) // 뒤로 버튼 클릭시 취소 가능 설정
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    // 확인 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        setResult(RESULT_CANCELED, outIntent);
                        finish();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    // 취소 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();    // 알림창 띄우기
    }
    
    // 11-06 Janghun Works : 다이나모 DB에 넣는 코드
    public void createDailyLog() {
        String date = edt_date.getText().toString();
        String s_content = edt_ContentText.getText().toString();
        Double d_battery = Double.parseDouble(edt_BatteryText.getText().toString());
        Double d_channel = Double.parseDouble(edt_ChannelText.getText().toString());
        Double d_temp = Double.parseDouble(edt_TempText.getText().toString());
        Double d_rootT = Double.parseDouble(edt_RootTText.getText().toString());
        Double d_humid = Double.parseDouble(edt_HumidText.getText().toString());
        Double d_co2 = Double.parseDouble(edt_Co2Text.getText().toString());
        Double d_soil = Double.parseDouble(edt_SoilTText.getText().toString());

        final com.example.ryu_w.calendar.DailyLogDO dailyLogItem = new com.example.ryu_w.calendar.DailyLogDO();

        dailyLogItem.setId("1106 test"); //sensor_p1 고정

        // 날짜
        dailyLogItem.setTime(date);
        // 일지 내용
        dailyLogItem.setContent(s_content);
        // CO2
        dailyLogItem.setCO2(d_co2);
        // 배터리
        dailyLogItem.setbat(d_battery);
        // 채널
        dailyLogItem.setchannel(d_channel);
        // 습도
        dailyLogItem.sethumi(d_humid);
        // 뿌리온도
        dailyLogItem.setrootT(d_rootT);
        // 온도
        dailyLogItem.settemp(d_temp);

        new Thread(new Runnable() {
            @Override
            public void run() {
                dynamoDBMapper.save(dailyLogItem);
                // Item saved

            }
        }).start();
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

        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_toNotification) {
            Intent intent = new Intent(getApplicationContext(), Notification.class);
            startActivity(intent);
        } else if (id == R.id.action_toCalendar) {
            Intent intent = new Intent(getApplicationContext(), Calendar.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}