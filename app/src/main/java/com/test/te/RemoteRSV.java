package com.test.te;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.io.IOException;


public class RemoteRSV extends Fragment {
    InfoUtils infoUtils;
    private ListView cValueList;
    private Button rsvAdd;
    public ValueListAdapter valueListAdapter;
    LayoutInflater inflater;
    CheckBox repeat;
    View remoteView;
    Thread t;
    boolean exit = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_remote_rsv, container, false);
        this.inflater=inflater;
        infoUtils = new InfoUtils();
        cValueList = view.findViewById(R.id.cValueList);
        valueListAdapter = new ValueListAdapter(view.getContext(),this);
        cValueList.setAdapter(valueListAdapter);
        rsvAdd = view.findViewById(R.id.rsvAdd);
        repeat = view.findViewById(R.id.repeat);
        remoteView = view;
        return  view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rsvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(remoteView.getContext(), RsvAddActivity.class);
                startActivityForResult(intent,1);
            }
        });
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
                                    int count = valueListAdapter.getCount();
                                    System.out.println("cc："+count);
                                    for (int i = 0; i <count; i++) {
                                        System.out.println(i);
                                        String data = ">" + Data.devices.get(Data.cDevicePosition).getDeviceID() + "&" + "03" + Data.dataLists.get(i).getAddress() + "0001#";
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
                                                String b =Data.dataLists.get(i).getMinUnit();
                                                b= b==null?"1":b;
                                                double v = Integer.parseInt(a, 16) * Double.parseDouble(b);
                                                if(!b.contains("."))
                                                {
                                                    Data.dataLists.get(i).setcValue("" + (int)v);
                                                }
                                                else
                                                {
                                                    Data.dataLists.get(i).setcValue("" + v);
                                                }
                                            }
                                        }
                                        Thread.sleep(400);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
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
                    Toast.makeText(getContext(),"停止循环读取",Toast.LENGTH_SHORT).show();
                    exit = true;
                }
            }
        });
    }
    public void notifyDataSetChanged()
    {
        valueListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        if(!exit)
        {
            exit = true;
        }
        super.onDestroy();
    }
}
