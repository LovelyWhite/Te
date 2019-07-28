package com.test.te;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.test.te.model.Device;

import java.io.IOException;

public class SelectActivity extends AppCompatActivity {
    private ListView deviceList;
    private Button addDevice;
    private DeviceListAdapter deviceListAdapter;
    private ProgressBar selectProgress;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private SelectActivity thisActivity;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        thisActivity = this;
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
            String deviceName = cursor.getString(cursor.getColumnIndex("deviceName"));
            Device temp = new Device();
            temp.setDeviceID(deviceID);
            temp.setDevicePW(devicePW);
            temp.setDeviceName(deviceName);
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
                    case 3: {
                        selectProgress.setVisibility(View.GONE);
                        Toast.makeText(thisActivity, "数据加载成功", Toast.LENGTH_LONG).show();
                        //创建Fragments并默认显示realtimeDATA
                        Intent intent = new Intent(SelectActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    break;
                    case -3: {
                        selectProgress.setVisibility(View.GONE);
                        Toast.makeText(thisActivity, "数据加载失败，请检查文件", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    break;
                    case 2: {
                        Toast.makeText(SelectActivity.this, "连接成功", Toast.LENGTH_LONG).show();
                        deviceList.setEnabled(true);
                    }break;
                    case 4: {
                        Toast.makeText(SelectActivity.this, "请输入设备ID和密码", Toast.LENGTH_LONG).show();
                        deviceList.setEnabled(true);
                    }
                }
                return false;
            }
        });

        Bundle b = getIntent().getExtras();
        InfoUtils.host = b.getString("ip");
        InfoUtils.port = b.getInt("port");
        deviceList = findViewById(R.id.deviceList);
        deviceListAdapter = new DeviceListAdapter(this);
        deviceList.setAdapter(deviceListAdapter);
        deviceList.setOnItemClickListener((parent, view, position, id) -> {
                    deviceList.setEnabled(false);
                    selectProgress.setVisibility(View.VISIBLE);
            Toast.makeText(SelectActivity.this, "正在连接服务器", Toast.LENGTH_LONG).show();
            new Thread(() -> {
                        InfoUtils infoUtils = new InfoUtils();
                        String data = "<" + Data.devices.get(position).getDeviceID() +
                                "&" +
                                Data.devices.get(position).getDevicePW() +
                                "#";
                        String result = null;
                        try {
                            result = infoUtils.sendData(data, null, null);
                            if (result.contains("&")) {
                                Data.devices.get(position).setSocket(infoUtils.getSocket());
                                Data.devices.get(position).setPosition(infoUtils.getPosition());

                                //加载参数数据
                                //存pCode
                                SharedPreferences userSettings = getSharedPreferences("pCodeShowed", 0);
                            //    System.out.println("aaa:"+Data.devices.size());
                                Data.showed = userSettings.getString(Data.devices.get(position).getDeviceID(), "");
                                Data.aShowed = userSettings.getString("a" + Data.devices.get(position).getDeviceID(), "");
//                        System.out.println(Data.showed);
                                final int REQUEST_EXTERNAL_STORAGE = 1;
                                String[] PERMISSIONS_STORAGE = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
                                int permission = ActivityCompat.checkSelfPermission(thisActivity, "android.permission.WRITE_EXTERNAL_STORAGE");
                                if (permission != PackageManager.PERMISSION_GRANTED) {
                                    // 没有写的权限，去申请写的权限，会弹出对话框
                                    ActivityCompat.requestPermissions(thisActivity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                                } else {
                                    final String b1 = Data.devices.get(position).getDeviceID().split("-")[0];
                                    Data.CopyAssets(thisActivity);
                                    boolean x = Data.getAccess(b1 + "parameterTable.mdb");
                                    if (x) {
                                        Message m = new Message();
                                        m.what = 3;//成功
                                        handler.sendMessage(m);
                                        Data.cDevicePosition = position;
                                    } else {
                                        Message m = new Message();
                                        m.what = -3;//失败
                                        handler.sendMessage(m);
                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }).start();
                });
        deviceList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                new AlertDialog.Builder(SelectActivity.this)
                        .setTitle("是否删除？")
                        .setPositiveButton("确定", (dialogInterface, i1) -> {
                            db = dbHelper.getReadableDatabase();
                            db.delete("Devices", "deviceID=?", new String[]{Data.devices.get(i).getDeviceID()});
                            Data.devices.remove(i);
                            deviceListAdapter.notifyDataSetChanged();
                        }).create().show();
                return true;
            }
        });
        //登录成功建立连接
        //连接信息保存在infoUtils 对象里
        //通过调用 sendData 就能发送数据
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                InfoUtils infoUtils = new InfoUtils();
//                StringBuilder stringBuilder = new StringBuilder("<");
//                for (int i = 0; i < Data.devices.size(); i++) {
//                    stringBuilder.append(Data.devices.get(i).getDeviceID());
//                    stringBuilder.append("&");
//                    stringBuilder.append(Data.devices.get(i).getDevicePW());
//                    if (i != Data.devices.size() - 1) {
//                        stringBuilder.append("&");
//                    }
//                }
//                stringBuilder.append("#");
//                String data = stringBuilder.toString();
//                if (!data.equals("<#")) {
//                    String result = null;
//                    try {
//                        result = infoUtils.sendData(data, null,null);
//                        if (result.contains("&")) {
//                            for (int i = 0; i < Data.devices.size(); i++) {
//                                Data.devices.get(i).setSocket(infoUtils.getSocket());
//                                Data.devices.get(i).setPosition(infoUtils.getPosition());
//                            }
//                            Message m = new Message();
//                            m.what = 2;//连接成功
//                            handler.sendMessage(m);
//                        } else {
//                            Message m = new Message();
//                            m.what = -2;//连接失败
//                            handler.sendMessage(m);
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        Message m = new Message();
//                        m.what = -2;//错误
//                        handler.sendMessage(m);
//                    }
//                } else {
//                    deviceList.setEnabled(true);
//                }
//            }
//        }).start();
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
                                final EditText newDeviceID = view.findViewById(R.id.newDeviceID);
                                final EditText newDevicePW = view.findViewById(R.id.newPassword);
                                final EditText newName = view.findViewById(R.id.newName);
                                final String name = newName.getText().toString();
                                final String id = newDeviceID.getText().toString().toUpperCase();
                                final String pw = newDevicePW.getText().toString();
                                if(!id.equals("")&&!pw.equals(""))
                                {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Cursor c = db.rawQuery("SELECT * FROM Devices WHERE deviceID ='" + id + "'", null);
//                                        System.out.println(c.getCount());
                                            if (c.getCount() == 1) {
                                                Message m = new Message();
                                                m.what = 1;//已存在
                                                handler.sendMessage(m);
                                            } else {
                                                InfoUtils infoUtils = new InfoUtils();
                                                String result = null;
                                                try {
                                                    result = infoUtils.sendData("<" + id + "&" + pw + "#", null, null);
                                                    if (result.contains("&")) {
                                                        ContentValues values = new ContentValues();
                                                        values.put("deviceName", name);
                                                        values.put("deviceID", id);
                                                        values.put("devicePW", pw);
                                                        db.insert("Devices", null, values);
                                                        Device temp = new Device();
                                                        temp.setDeviceName(name);
                                                        temp.setDeviceID(id);
                                                        temp.setDevicePW(pw);
                                                        temp.setSocket(infoUtils.getSocket());
                                                        temp.setPosition(infoUtils.getPosition());
                                                        Data.devices.add(temp);
                                                        Message m = new Message();
                                                        m.what = 0;//验证成功
                                                        handler.sendMessage(m);
                                                    } else {
                                                        Message m = new Message();
                                                        m.what = -1;//验证失败
                                                        handler.sendMessage(m);
                                                    }
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            c.close();
                                        }
                                    }).start();
                                }
                                else
                                {
                                    Message m = new Message();
                                    m.what = 4;//验证失败
                                    handler.sendMessage(m);
                                }
                            }
                        }).create().show();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        deviceList.setEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int grantResult : grantResults) {
                if (grantResult != 0) {
                    Toast.makeText(this, "未获取权限", Toast.LENGTH_LONG).show();
                    finish();
                    break;
                }
            }
        }
        final String b = Data.devices.get(Data.cDevicePosition).getDeviceID().split("-")[0];
        Data.CopyAssets(thisActivity);
        boolean x = Data.getAccess(b + "parameterTable.mdb");
        if (x) {
            Message m = new Message();
            m.what = 3;//成功
            handler.sendMessage(m);
        } else {
            Message m = new Message();
            m.what = -3;//失败
            handler.sendMessage(m);
        }
    }
}