package com.example.ryu_w.calendar;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telecom.Call;
import android.test.UiThreadTest;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.ReturnConsumedCapacity;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.CheckedFuture;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Calendar extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // 변수 선언   1104 GIT TEST    i added this
    MaterialCalendarView materialCalendarView;
    DBHelper myHelper;
    SQLiteDatabase sqlDB;
    public static Context context;
    public boolean sv_isvisible = false;
    public ScrollView sv1;
    public Cursor cursor;
    public String[] result;
    public String send_date = "";

    static DynamoDBMapper dynamoDBMapper; //사용해야할 코드(1-1)!!

    static List<String> date_list = new ArrayList<String>();
    static List<String> arr_date = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        // (1) activity_index에서 include된 Acitivity 띄우기
        ViewFlipper viewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper);
        viewFlipper.setDisplayedChild(3); // ~ (1)

        // (2) Navigation_drawer Activity에 필요한 메뉴바 띄우기
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // DynamoDB 연결
        AWSMobileClient.getInstance().initialize(this).execute();

        AWSCredentialsProvider credentialsProvider = AWSMobileClient.getInstance().getCredentialsProvider();
        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();

        // AmazonDynamoDBClient를 인스턴스화하는 코드 추가
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(credentialsProvider);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this); // ~ (2)


        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(configuration)
                .build();

        loadDailyLog();

//       // DB 연결//
//       String dbName = "TKLabsDB";
//       String databasePath = getFilesDir().getPath() + "/" + dbName;
//       myHelper = new DBHelper(Calendar.this, databasePath, null);
//       sqlDB = myHelper.getReadableDatabase();

         context = this;

         // 캘린더 만들기 //
         materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
         materialCalendarView.setTopbarVisible(false);

         materialCalendarView.state().edit()
                .setFirstDayOfWeek(java.util.Calendar.SUNDAY)
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

         top_date(); // 상달 월/년 표시 함수

         materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                new onDayDecorator()
         );

         // 달 바뀔 때 위에 topBar 달,년 바뀌게 //
         materialCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                top_date();
            }
        });

         // 캘린더 - 특정일 지정 //
//      cursor = sqlDB.rawQuery("SELECT datetime FROM plantTable;", null);;

//        Log.i("@@ date_list", date_list.toString());
//        result = new String[date_list.size()];
//        date_list.toArray(result);
        new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());

        // 캘린더 - 클릭이벤트 //
        sv1 = (ScrollView) findViewById(R.id.sv);
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                TextView tv_nodata = (TextView) findViewById(R.id.no_data);
                TextView tv_data = (TextView) findViewById(R.id.data);
                sv1.setVisibility(View.VISIBLE);
                sv_isvisible = true;
                int i = 0;
                String datetime = date.getYear() + "-" +
                        (date.getMonth() > 8 ? date.getMonth() + 1 : "0" + (date.getMonth() + 1)) + "-" +
                        (date.getDay() > 9 ? date.getDay() : "0" + date.getDay());
                send_date = datetime; //Intent를 통해 WriteDailyLog로 넘길 날짜 저장

                // DB없으면 날짜 선택 시 에러나서 에러 처리,,
                if (result.length == 0) {
                    result = new String[1];
                    result[0] = "";
                }

                while (datetime.equals(result[i]) == false) {
                    i++;
                    if (i >= result.length) break;
                }
                if (i < result.length) { //저장한 데이터가 있을 경우
                    String s_data_view = "";
                    for (int j = 0; j < data_view(datetime).length; j++) {  // : TODO
                        s_data_view += data_view(datetime)[j] + "\n\n";
                    }
                    tv_data.setText(s_data_view);
                    tv_nodata.setVisibility(View.GONE);
                    tv_data.setVisibility(View.VISIBLE);
                } else { // 저장한 데이터가 없을 경우
                    tv_nodata.setVisibility(View.VISIBLE);
                    tv_data.setVisibility(View.GONE);
                }
            }
        });

        // 데이터 쓰기 버튼 //
        Button btn_Add = (Button) findViewById(R.id.btn_add);
        btn_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WriteDailyLog.class);
                intent.putExtra("send_date", send_date); // 선택 날짜 데이터 보냄
                startActivityForResult(intent, 0);
            }
        });

        // 오늘 버튼 //
        Button btn_Today = (Button) findViewById(R.id.btn_today);
        btn_Today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarDay today = CalendarDay.today(); // 오늘 날짜 받아옴.
                materialCalendarView.setCurrentDate(today, true); // 오늘 날짜로 설정.
                top_date();
            }
        });
    }

    // 점찍는 API
    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        String[] Time_Result;
        ApiSimulator(String[] Time_Result){
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            java.util.Calendar calendar = java.util.Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 -를 기준으로짜르고 string을 int 로 변환
            for(int i = 0 ; i < Time_Result.length ; i ++){
                String[] time = Time_Result[i].split("-");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                calendar.set(year,month-1,dayy);
                CalendarDay day = CalendarDay.from(calendar);
                dates.add(day);
            }
            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (isFinishing()) {
                return;
            }

            materialCalendarView.addDecorator(new EventDecorator(Color.RED, calendarDays));
        }
    }

    public String[] data_view(String date) {
        cursor = sqlDB.rawQuery("SELECT * FROM plantTable WHERE datetime like '" + date + "%';", null);;  // :TODO
        final List<String> arr_data = new ArrayList<String>();
        while(cursor.moveToNext()){
            arr_data.add("기온 : " + cursor.getString(3) + "℃  뿌리 온도 : " + cursor.getString(4) + "℃\n습도 : " +
                    cursor.getString(5) + "%  CO2 : "+ cursor.getString(6) + "ppm");
        }
        String[] arr_result;
        arr_result = new String[arr_data.size()];
        arr_data.toArray(arr_result);
        cursor.close();
        return arr_result;
    }

    // 종료 뒤로가기 출처: http://best421.tistory.com/71 [Updates available]
    long pressedTime = 0;
    @Override
    public void onBackPressed() {
        if (sv_isvisible == true){
            sv1.setVisibility(View.INVISIBLE);
            sv_isvisible = false;
        } else if ( pressedTime == 0 ) {
            Toast.makeText(Calendar.this, " 한 번 더 누르시면 종료됩니다." , Toast.LENGTH_SHORT).show();
            pressedTime = System.currentTimeMillis();
        } else {
            int seconds = (int) (System.currentTimeMillis() - pressedTime);

            if ( seconds > 1500 ) {
                Toast.makeText(Calendar.this, " 한 번 더 누르시면 종료됩니다." , Toast.LENGTH_SHORT).show();
                pressedTime = 0 ;
            }
            else {
                sqlDB.close();
                super.onBackPressed();
                finish(); // app 종료 시키기
            }
        }
    }

    static String[] currentDay;
    public void top_date(){
        // 월, 년 표시M
        TextView tv_Month = (TextView)findViewById(R.id.month);
        TextView tv_Year = (TextView)findViewById(R.id.year);
        CalendarDay crntDay = materialCalendarView.getCurrentDate();
        currentDay = crntDay.toString().substring(12,19).split("-");
        tv_Month.setText(Integer.parseInt(currentDay[1]) + 1 +"월");
        tv_Year.setText(currentDay[0]);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){ // RESULT_OK를 보냈으면 값을 받아옴
            finish();
            startActivity(data);
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

    public void loadDailyLog()  {

        Condition rangeKeyCondition = new Condition()   // 쿼리 검색 조건 설정
                .withComparisonOperator(ComparisonOperator.GT)
                .withAttributeValueList(new AttributeValue().withS("1"));

        DailyLogDO dailylogKey = new DailyLogDO();  // 쿼리 검색 해쉬 값 설정
        dailylogKey.setId("sensor_p1");

        final DynamoDBQueryExpression<DailyLogDO> queryExpression = new DynamoDBQueryExpression<DailyLogDO>()   // 쿼리식 작성
                .withHashKeyValues(dailylogKey)
                .withRangeKeyCondition("time", rangeKeyCondition);

        // ------------------------------------------------------------    Callable

        ExecutorService exec = Executors.newSingleThreadExecutor();
        List<Future<List<String>>> list = new ArrayList<Future<List<String>>>();
        Future<List<String>> future = exec.submit(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                try {
                    List<DailyLogDO> save_date = dynamoDBMapper.query(DailyLogDO.class, queryExpression);
                    int i = 0;
                    while (i < save_date.size()) {
                        date_list.add(save_date.get(i).getTime());
                        i++;
                    }
                    result = new String[date_list.size()];
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
                return date_list;
            }

        });

        list.add(future);

        for(Future<List<String>> fut : list){
            try{
                String str_result = (fut.get().toString());
                str_result = str_result.substring(1,str_result.length()-1);
                result = str_result.split(", ");
            } catch (InterruptedException | ExecutionException e){
                e.printStackTrace();
            }
        }
    }

}


