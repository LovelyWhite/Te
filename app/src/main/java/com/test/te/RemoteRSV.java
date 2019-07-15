package com.test.te;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
    private Spinner selectData;
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
        selectData = view.findViewById(R.id.selectData);
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,Data.tableList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectData.setAdapter(adapter);
        selectData.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if(t!=null&&t.isAlive())
                {
                    Toast.makeText(getContext(),"请关闭或等待循环读取结束",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Data.nowTable=arg2;
                    valueListAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
                    exit = false;
                    t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (!exit)
                            {
                                try {
                                    int count = valueListAdapter.getCount();
                                    for (int i = 0; i <count; i++) {
                                        if(exit)
                                        {
                                            break;
                                        }
                                        String data = ">" + Data.devices.get(Data.cDevicePosition).getDeviceID() + "&" + "03" + Data.dataLists.get(Data.tableList.get(Data.nowTable)).get(i).getAddress() + "0001#";
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
                                                String b =Data.dataLists.get(Data.tableList.get(Data.nowTable)).get(i).getMinUnit();
                                                b= b==null?"1":b;
                                                double v = Integer.parseInt(a, 16) * Double.parseDouble(b);
                                                if(!b.contains("."))
                                                {
                                                    Data.dataLists.get(Data.tableList.get(Data.nowTable)).get(i).setcValue("" + (int)v);
                                                }
                                                else
                                                {
                                                    Data.dataLists.get(Data.tableList.get(Data.nowTable)).get(i).setcValue("" + v);
                                                }
                                            }
                                        }
                                        Thread.sleep(2000);
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
        if(t.isAlive())
        {
            exit = true;
        }
        super.onDestroy();
    }
}
