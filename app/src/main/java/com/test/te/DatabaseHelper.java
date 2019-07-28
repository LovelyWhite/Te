package com.test.te;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {
    private  Context context;
    DatabaseHelper(Context context,String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL="create table Devices(" +
                "deviceID text primary key," +
                "deviceName text,"+
                "devicePW text)";
        db.execSQL(SQL);
        SQL="create table Ip(" +
                "ip text primary key," +
                "port text)";
        db.execSQL(SQL);
        Toast.makeText(context,"本地设备数据库创建成功",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
