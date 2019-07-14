package com.test.te;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.test.te.model.Device;

public class LoginActivity extends AppCompatActivity {
    private EditText account;
    private EditText password;
    private EditText ip;
    private EditText port;
    private Button login;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        account = findViewById(R.id.account);
        password = findViewById(R.id.password);
        ip = findViewById(R.id.ip);
        port = findViewById(R.id.port);
        login = findViewById(R.id.login);
        dbHelper = new DatabaseHelper(this, "info.db", null, 1);
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Ip", null, null, null, null, null, null, null);
        while( cursor.moveToNext())
        {
            String ip = cursor.getString(cursor.getColumnIndex("ip"));
            String port = cursor.getString(cursor.getColumnIndex("port"));
            this.ip.setText(ip);
            this.port.setText(port);
        }
        //查询指针关闭
        cursor.close();


        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        {
                      //  Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                        //清空表
                        db.execSQL("DELETE FROM Ip WHERE 1==1");
                        ContentValues values = new ContentValues();
                        values.put("ip", ip.getText().toString());
                        values.put("port", port.getText().toString());
                        db.insert("Ip", null, values);
                        Intent intent = new Intent(LoginActivity.this, SelectActivity.class);
                        intent.putExtra("ip",ip.getText().toString());
                        intent.putExtra("port",Integer.parseInt(port.getText().toString()));
                        startActivity(intent);
                    }
                    break;
                    case -1: {
                        Toast.makeText(LoginActivity.this, "账号或密码错误", Toast.LENGTH_LONG).show();
                    }
                }
                login.setEnabled(true);
                return false;
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if(ip.getText().toString().equals("")||port.getText().toString().equals(""))
               {
                   Toast.makeText(LoginActivity.this, "请输入IP和port", Toast.LENGTH_LONG).show();
               }
               else
               {
                   login.setEnabled(false);
                   new Thread(new Runnable() {
                       @Override
                       public void run() {
                           if (true) {
                               Message m = new Message();
                               m.what = 0;//登录成功
                               handler.sendMessage(m);
                           } else {
                               Message m = new Message();
                               m.what = -1;//登录失败
                               handler.sendMessage(m);
                           }
                       }
                   }).start();
               }
            }
        });
    }
}
