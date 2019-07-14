package com.test.te;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.test.te.model.Alert;
import com.test.te.model.Device;

import java.io.IOException;

public class SelectActivity extends AppCompatActivity {
    private ListView deviceList;
    private Button addDevice;
    private DeviceListAdapter deviceListAdapter;
    private ProgressBar selectProgress;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
//        Intent intent = new Intent(SelectActivity.this, MainActivity.class);
//        startActivity(intent);
        selectProgress = findViewById(R.id.selectProgress);
        //获取本地存储的数据库 device.db
        dbHelper = new DatabaseHelper(this, "info.db", null, 1);
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Devices", null, null, null, null, null, null, null);
        //在添加之前先清空当前内存中的数据
        Data.devices.clear();

        while (cursor.moveToNext()) {
            String deviceID = cursor.getString(cursor.getColumnIndex("deviceID"));
            String devicePW = cursor.getString(cursor.getColumnIndex("devicePW"));
            Device temp = new Device();
            temp.setDeviceID(deviceID);
            temp.setDevicePW(devicePW);
            Data.devices.add(temp);
        }
        //查询指针关闭
        cursor.close();

        //handler 事件处理
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 0: {
                        deviceListAdapter.notifyDataSetChanged();
                        Toast.makeText(SelectActivity.this, "添加成功", Toast.LENGTH_LONG).show();
                    }
                    break;
                    case 1: {
                        Toast.makeText(SelectActivity.this, "已存在，添加失败", Toast.LENGTH_LONG).show();
                    }
                    break;
                    case -1: {
                        Toast.makeText(SelectActivity.this, "添加失败,请检查数据", Toast.LENGTH_LONG).show();
                    }
                    break;
                    case -2: {
                        Toast.makeText(SelectActivity.this, "连接错误", Toast.LENGTH_LONG).show();
                    }
                    break;
                    case 2: {
                        Toast.makeText(SelectActivity.this, "连接成功", Toast.LENGTH_LONG).show();
                        deviceList.setEnabled(true);
                    }
                }
                selectProgress.setVisibility(View.INVISIBLE);
                return false;
            }
        });


        Bundle b = getIntent().getExtras();
        InfoUtils.host = b.getString("ip");
        InfoUtils.port = b.getInt("port");

        deviceList = findViewById(R.id.deviceList);
        deviceListAdapter = new DeviceListAdapter(this);
        deviceList.setAdapter(deviceListAdapter);
        deviceList.setEnabled(false);
        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Data.cDevicePosition = position;
                deviceList.setEnabled(false);
                Intent intent = new Intent(SelectActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        deviceList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(SelectActivity.this)
                        .setTitle("确认删除？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.execSQL("DELETE FROM Devices WHERE deviceID='" + Data.devices.get(position).getDeviceID() + "'");
                                Data.devices.remove(position);
                                deviceListAdapter.notifyDataSetChanged();
                            }
                        }).show();
                return true;
            }
        });
        //登录成功建立连接
        //连接信息保存在infoUtils 对象里
        //通过调用 sendData 就能发送数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                InfoUtils infoUtils = new InfoUtils();
                selectProgress.setVisibility(View.VISIBLE);
                StringBuilder stringBuilder = new StringBuilder("<");
                for (int i = 0; i < Data.devices.size(); i++) {
                    stringBuilder.append(Data.devices.get(i).getDeviceID());
                    stringBuilder.append("&");
                    stringBuilder.append(Data.devices.get(i).getDevicePW());
                    if (i != Data.devices.size() - 1) {
                        stringBuilder.append("&");
                    }
                }
                stringBuilder.append("#");
                String data = stringBuilder.toString();
                if (!data.equals("<#")) {
                    String result = null;
                    try {
                        result = infoUtils.sendData(data, null,null);
                        if (result.contains("&")) {
                            for (int i = 0; i < Data.devices.size(); i++) {
                                Data.devices.get(i).setSocket(infoUtils.getSocket());
                                Data.devices.get(i).setPosition(infoUtils.getPosition());
                            }
                            Message m = new Message();
                            m.what = 2;//连接成功
                            handler.sendMessage(m);
                        } else {
                            Message m = new Message();
                            m.what = -2;//连接失败
                            handler.sendMessage(m);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Message m = new Message();
                        m.what = -2;//错误
                        handler.sendMessage(m);
                    }
                } else {
                    selectProgress.setVisibility(View.INVISIBLE);
                    deviceList.setEnabled(true);
                }
            }
        }).start();
        addDevice = findViewById(R.id.addDevice);
        addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = getLayoutInflater().inflate(R.layout.add_device, null);
                new AlertDialog.Builder(SelectActivity.this)
                        .setTitle("添加新设备")
                        .setView(view)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectProgress.setVisibility(View.VISIBLE);
                                final EditText newDeviceID = view.findViewById(R.id.newDeviceID);
                                final EditText newDevicePW = view.findViewById(R.id.newPassword);
                                final String id = newDeviceID.getText().toString();
                                final String pw = newDevicePW.getText().toString();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        Cursor c = db.rawQuery("SELECT * FROM Devices WHERE deviceID ='" + id + "'", null);
                                        System.out.println(c.getCount());
                                        if (c.getCount() == 1) {
                                            Message m = new Message();
                                            m.what = 1;//已存在
                                            handler.sendMessage(m);
                                        } else {
                                            InfoUtils infoUtils = new InfoUtils();
                                            String result = null;
                                            try {
                                                result = infoUtils.sendData("<" + id + "&" + pw + "#", null,null);
                                                if (result.contains("&")) {
                                                    ContentValues values = new ContentValues();
                                                    values.put("deviceID", id);
                                                    values.put("devicePW", pw);
                                                    db.insert("Devices", null, values);
                                                    Device temp = new Device();
                                                    temp.setDeviceID(id);
                                                    temp.setDevicePW(pw);
                                                    temp.setSocket(infoUtils.getSocket());
                                                    Data.devices.add(temp);
                                                    Message m = new Message();
                                                    m.what = 0;//验证成功
                                                    handler.sendMessage(m);
                                                } {
                                                    Message m = new Message();
                                                    m.what = -1;//验证失败
                                                    handler.sendMessage(m);
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                Message m = new Message();
                                                m.what = -2;//错误
                                                handler.sendMessage(m);
                                            }
                                        }
                                        c.close();
                                    }
                                }).start();
                            }
                        }).create().show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        Toast.makeText(SelectActivity.this, "正在断开连接", Toast.LENGTH_LONG).show();
        for (Device device : Data.devices) {
            if (!device.getSocket().isClosed()) {
                try {
                    device.getSocket().close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        deviceList.setEnabled(true);
    }
}