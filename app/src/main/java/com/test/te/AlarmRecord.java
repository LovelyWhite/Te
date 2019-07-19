package com.test.te;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;


public class AlarmRecord extends Fragment {

    ListView alertList;
    CheckBox repeat;
    boolean exit =false;
    Thread t;
    public AlertListAdapter alertListAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_record, container, false);
        alertList = view.findViewById(R.id.alertList);
        repeat = view.findViewById(R.id.repeat);
        alertListAdapter = new AlertListAdapter(view.getContext(),this);
        alertList.setAdapter(alertListAdapter);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final InfoUtils infoUtils = new InfoUtils();
        repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    Toast.makeText(getContext(),"开始循环读取",Toast.LENGTH_SHORT).show();
                    exit = false;
                    t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (!exit)
                            {
                                try {
                                    int count = alertListAdapter.getCount();
                                    System.out.println("cc："+count);
                                    for (int i = 0; i <count; i++) {
                                        System.out.println(i);
                                        if(exit)
                                        {

                                            Looper.prepare();
                                            Toast.makeText(getContext(),"停止成功",Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                            break;
                                        }
                                        String data = ">" + Data.devices.get(Data.cDevicePosition).getDeviceID() + "&" + "03" + Data.alerts.get(i).getAddress() + "0001#";
                                        if(data.contains("null"))
                                        {
                                        }
                                        else
                                        {
                                            String result = infoUtils.sendData(data
                                                    , Data.devices.get(Data.cDevicePosition).getSocket()
                                                    ,Data.devices.get(Data.cDevicePosition).getPosition());
                                            result = result==null?"":result;
                                            if (result.contains("Drive No online")) {
                                            } else if(!result.equals("")) {
                                                String a = result.split("&")[1].substring(4, 8);
                                                String b =Data.alerts.get(i).getMinUnit();
                                                b= b==null?"1":b;
                                                double v = Integer.parseInt(a, 16) * Double.parseDouble(b);
                                                if(!b.contains("."))
                                                {
                                                    Data.alerts.get(i).setcValue("" + (int)v);
                                                }
                                                else
                                                {
                                                    Data.alerts.get(i).setcValue("" + v);
                                                }
                                            }
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    t.start();
                }
                else
                {
                    Toast.makeText(getContext(),"正在停止",Toast.LENGTH_SHORT).show();
                    exit = true;
                }
            }
        });
    }

    public void notifyDataSetChanged()
    {
        alertListAdapter.notifyDataSetChanged();
    }



}
