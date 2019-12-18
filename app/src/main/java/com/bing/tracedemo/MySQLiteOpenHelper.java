package com.bing.tracedemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    final String CREATE_DATABASE = "create table trace(" +
            "id integer primary key autoincrement," +
            "time text," +
            "location text," +
            "points text)";

    public MySQLiteOpenHelper(Context context){
        super(context,"trace",null,1);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DATABASE);
        Log.d("helper","创建数据库成功");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
