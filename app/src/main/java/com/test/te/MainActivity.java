package com.test.te;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.test.te.model.Alert;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private String preTag = null;
    RemoteRSV remoteRSV = new RemoteRSV();
   // RealTimeData realTimeData = new RealTimeData();
    RemoteControl remoteControl = new RemoteControl();
    AlarmRecord alarmRecord = new AlarmRecord();
    HistoricalCurve historicalCurve = new HistoricalCurve();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
//                case R.id.realTimeData://实时数据
//                    showFragment(preTag, "realTimeData");
//                    preTag = "realTimeData";
//                    return true;
                case R.id.RemoteControl://远程控制
                    showFragment(preTag, "remoteControl");
                    preTag = "remoteControl";
                    return true;
                case R.id.RemoteRSV://远程读设参
                    showFragment(preTag, "remoteRSV");
                    preTag = "remoteRSV";
                    return true;
                case R.id.AlarmRecord://报警记录
                    showFragment(preTag, "alarmRecord");
                    preTag = "alarmRecord";
                    return true;
                case R.id.HistoricalCurve://历史曲线
                    showFragment(preTag, "historicalCurve");
                    preTag = "historicalCurve";
                    return true;
            }
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //120.41.250.52
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        boolean x =false;
        //加载参数数据
        //存pCode
        SharedPreferences userSettings = getSharedPreferences("pCodeShowed", 0);
        Data.showed = userSettings.getString(Data.devices.get(Data.cDevicePosition).getDeviceID(),"");
        //  Data.getExcel("DeviceCode",this);
        //模拟数据
        Alert alert = new Alert();
        alert.setAlertFreq("112");
        alert.setAlertEle("122");
        alert.setAlertTime("20:00");
        alert.setAlertID("111");
        alert.setAlertDate("2019/09/21");
        Data.alerts.add(alert);

        final int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE" };
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
            else
            {
                String b =  Data.devices.get(Data.cDevicePosition).getDeviceID().split("-")[0];
                Toast.makeText(MainActivity.this,"正在加载数据",Toast.LENGTH_LONG).show();
                Data.CopyAssets(this);
                x = Data.getAccess(b+"parameterTable.mdb");
                long now = System.currentTimeMillis();
                while (System.currentTimeMillis()-now<15000&& !x) {
                }
                if(x)
                {
                    Toast.makeText(MainActivity.this,"数据加载成功",Toast.LENGTH_LONG).show();
                    //创建Fragments并默认显示realtimeDATA
                    buildFragments();
                    Data.mainActivity = this;
                }
                else
                {
                    Toast.makeText(MainActivity.this,"数据加载失败，请检查文件",Toast.LENGTH_LONG).show();
                    finish();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    void buildFragments() {
        getSupportFragmentManager().beginTransaction()
              //  .add(R.id.frameLayout, realTimeData, "realTimeData")
                .add(R.id.frameLayout, remoteControl, "remoteControl")
                .add(R.id.frameLayout, remoteRSV, "remoteRSV")
                .add(R.id.frameLayout, alarmRecord, "alarmRecord")
                .add(R.id.frameLayout, historicalCurve, "historicalCurve")
                .commit();
        getSupportFragmentManager().beginTransaction()
               // .hide(remoteControl)
                .hide(remoteRSV)
                .hide(alarmRecord)
                .hide(historicalCurve)
                .commit();
        preTag="remoteControl";
       // realTimeData.startFlush();
    }

    void showFragment(String preTag, String tag) {
      //  if(tag.equals("realTimeData"))
        {
            //开始刷新
      //      realTimeData.startFlush();
        }
     //   else
        {
      //      realTimeData.stopFlush();
        }
        if (!preTag.equals(tag)) {
            getSupportFragmentManager().beginTransaction().hide(Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(preTag)))
                    .show(Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(tag)))
                    .commit();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1)
        {
            remoteRSV.notifyDataSetChanged();
        }
    }
    public void commit() {
        SharedPreferences userSettings = getSharedPreferences("pCodeShowed", 0);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putString(Data.devices.get(Data.cDevicePosition).getDeviceID(), Data.showed);
        editor.apply();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1)
        {
            for (int grantResult : grantResults) {
                if(grantResult!=0)
                {
                    Toast.makeText(MainActivity.this,"未获取权限",Toast.LENGTH_LONG).show();
                    finish();
                    break;
                }
            }
        }
        Data.getAccess("CV3100ParameterTable.mdb");
    }

    @Override
    protected void onDestroy() {
        Data.alerts.clear();
        if(alarmRecord!=null && alarmRecord.alertListAdapter!=null)
        {
            alarmRecord.notifyDataSetChanged();
        }
        Data.showed = "";
        //远程读设参的数组
        Data.dataLists.clear();
        Data.allpCode.clear();
        if(remoteRSV!=null&&remoteRSV.valueListAdapter!=null)
        {
            remoteRSV.notifyDataSetChanged();
        }
        Data.tableList.clear();
        Data.nowTable = 0;
        Data.cDevicePosition = -1;
        super.onDestroy();
    }
}
