package com.test.te;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;


public class AlarmRecord extends Fragment {

    ListView alertList;
    Button read,add;

    public AlertListAdapter alertListAdapter;
    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_record, container, false);
        alertList = view.findViewById(R.id.alertList);
        read = view.findViewById(R.id.read);
        add = view.findViewById(R.id.add);
        alertListAdapter = new AlertListAdapter(view.getContext(),this);
        alertList.setAdapter(alertListAdapter);
        handler = new Handler(message -> {
            switch (message.what)
            {
                case 1:
                    notifyDataSetChanged();
                    Toast.makeText(getContext(),"读取完成",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(getContext(),"开始读取",Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        });
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final InfoUtils infoUtils = new InfoUtils();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AlertAddActivity.class);
                startActivityForResult(intent,2);
            }
        });
        read.setOnClickListener(view1 -> {
          new Thread(() -> {
              try
              {
                  Message m = new Message();
                  m.what = 2;
                  handler.sendMessage(m);
                  int count = alertListAdapter.getCount();
                  for (int i = 0; i <count; i++) {
                      System.out.println(i);

                      String data = ">" + Data.devices.get(Data.cDevicePosition).getDeviceID() + "&" + "03" + Data.dataAlerts.get(i).getAddress() + "0001#";
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
                              String b =Data.dataAlerts.get(i).getMinUnit();
                              b= b==null?"1":b;
                              double v = Integer.parseInt(a, 16) * Double.parseDouble(b);
                              if(!b.contains("."))
                              {
                                  Data.dataAlerts.get(i).setcValue("" + (int)v);
                              }
                              else
                              {
                                  Data.dataAlerts.get(i).setcValue("" + v);
                              }
                          }
                      }
                  }
                  m = new Message();
                  m.what = 1;
                  handler.sendMessage(m);
              }
              catch (IOException e) {
                  e.printStackTrace();
              }
          }).start();
        });
     }

    public void notifyDataSetChanged()
    {
        alertListAdapter.notifyDataSetChanged();
    }



}
