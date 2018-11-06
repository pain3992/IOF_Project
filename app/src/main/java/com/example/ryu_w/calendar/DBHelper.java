package com.example.ryu_w.calendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory){
        super(context, name, factory, 1);
    }

    String table_name = "plantTable";
    @Override
    public void onCreate(SQLiteDatabase db){

        db.execSQL("CREATE TABLE IF NOT EXISTS " + table_name + " (datetime TEXT, battery LONG, channel LONG, tmp LONG, rootT LONG, humid LONG, co2 LONG, soilT LONG);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS plantTable");
        onCreate(db);
    }

}
