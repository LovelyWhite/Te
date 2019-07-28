package com.test.te;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.test.te.model.Device;

import java.io.IOException;
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
//            System.out.println("t1:"+Data.t1);
//            System.out.println("t2:"+Data.t2);
            if(Data.t1==null&&Data.t2==null)
            {
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
            }
            else
            {
                Toast.makeText(getApplicationContext(),"当前任务未停止",Toast.LENGTH_LONG).show();
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
        TextView textView = findViewById(R.id.currentDevice);
        textView.setText(Data.devices.get(Data.cDevicePosition).getDeviceName()+"--"+Data.devices.get(Data.cDevicePosition).getDeviceID());
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        buildFragments();
        Data.mainActivity = this;

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
        else if(resultCode==2)
        {
            alarmRecord.notifyDataSetChanged();
        }
    }
    public void commit() {
        SharedPreferences userSettings = getSharedPreferences("pCodeShowed", 0);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putString(Data.devices.get(Data.cDevicePosition).getDeviceID(), Data.showed);
        editor.putString("a"+Data.devices.get(Data.cDevicePosition).getDeviceID(), Data.aShowed);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        Toast.makeText(MainActivity.this, "正在断开连接", Toast.LENGTH_LONG).show();
        Data.dataAlerts.clear();
        Data.allAlerts.clear();
        if(alarmRecord!=null && alarmRecord.alertListAdapter!=null)
        {
            alarmRecord.notifyDataSetChanged();
        }
        Data.showed = "";
        Data.aShowed = "";
        //远程读设参的数组
        Data.dataLists.clear();
        Data.allpCode.clear();
        if(remoteRSV!=null&&remoteRSV.valueListAdapter!=null)
        {
            remoteRSV.notifyDataSetChanged();
        }
        if(alarmRecord!=null&&alarmRecord.alertListAdapter!=null)
        {
            alarmRecord.notifyDataSetChanged();
        }
        Data.nowTable = 0;
        Data.cDevicePosition = -1;
        for (Device device : Data.devices) {
            if (device.getSocket()!=null&& !device.getSocket().isClosed()) {
                try {
                    device.getSocket().close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        super.onDestroy();
    }
}
