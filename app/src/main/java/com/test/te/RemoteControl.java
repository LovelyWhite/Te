package com.test.te;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;


public class RemoteControl extends Fragment {

    DashBoard freq;
    DashBoard ampere;
    Button start,stop,setFreq;
    EditText freqNum;
    Handler handler;
    InfoUtils infoUtils = new InfoUtils();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_remote_control, container, false);
       freq = view.findViewById(R.id.freq);
       ampere = view.findViewById(R.id.ampere);
       start = view.findViewById(R.id.start);
       stop = view.findViewById(R.id.stop);
       setFreq = view.findViewById(R.id.set_freq);
       freqNum = view.findViewById(R.id.freq_num);
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case -1:
                        Toast.makeText(getContext(), "请输入值", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Toast.makeText(getContext(), "成功", Toast.LENGTH_LONG).show();
                        break;
                    case -2:
                        Toast.makeText(getContext(), "设备不在线", Toast.LENGTH_LONG).show();
                        break;
                    case -3:
                        Toast.makeText(getContext(), "失败", Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });
       return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        freq.setRealTimeValue(1.2);
        ampere.setRealTimeValue(11);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String result = infoUtils.sendData(">" + Data.devices.get(Data.cDevicePosition).getDeviceID() + "&" + "06" + Data.ctrl.getAd_start() + Data.ctrl.getStart() + "#"
                                    , Data.devices.get(Data.cDevicePosition).getSocket()
                                    , Data.devices.get(Data.cDevicePosition).getPosition());
                            if(result.contains("Drive No online"))
                            {
                                Message m = new Message();
                                m.what = -2;
                                handler.sendMessage(m);
                            }
                            else if(result.contains("&"))
                            {
                                Message m = new Message();
                                m.what = 1;
                                handler.sendMessage(m);
                            }
                            else
                            {
                                Message m = new Message();
                                m.what = -3;
                                handler.sendMessage(m);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        start.setEnabled(true);
                    }
                }).start();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String result = infoUtils.sendData(">" + Data.devices.get(Data.cDevicePosition).getDeviceID() + "&" + "06" + Data.ctrl.getAd_stop() + Data.ctrl.getStop() + "#"
                                    , Data.devices.get(Data.cDevicePosition).getSocket()
                                    , Data.devices.get(Data.cDevicePosition).getPosition());
                            if(result.contains("Drive No online"))
                            {
                                Message m = new Message();
                                m.what = -2;
                                handler.sendMessage(m);
                            }
                            else if(result.contains("&"))
                            {
                                Message m = new Message();
                                m.what = 1;
                                handler.sendMessage(m);
                            }
                            else
                            {
                                Message m = new Message();
                                m.what = -3;
                                handler.sendMessage(m);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        stop.setEnabled(true);
                    }
                }).start();
            }
        });
        setFreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(setFreq.getText().equals(""))
                        {
                            Message m = new Message();
                            m.what = -1;
                            handler.sendMessage(m);
                        }
                        else
                        {
                            try {
                                String num =  setFreq.getText().toString();
                                int n = Integer.parseInt(num);
                                n*=100;
                                num =  Integer.toHexString(n);
                                String result = infoUtils.sendData(">" + Data.devices.get(Data.cDevicePosition).getDeviceID() + "&" + "06" + Data.ctrl.getAd_freq() + num + "#"
                                        , Data.devices.get(Data.cDevicePosition).getSocket()
                                        , Data.devices.get(Data.cDevicePosition).getPosition());
                                if(result.contains("Drive No online"))
                                {
                                    Message m = new Message();
                                    m.what = -2;
                                    handler.sendMessage(m);
                                }
                                else if(result.contains("&"))
                                {
                                    Message m = new Message();
                                    m.what = 1;
                                    handler.sendMessage(m);
                                }
                                else
                                {
                                    Message m = new Message();
                                    m.what = -3;
                                    handler.sendMessage(m);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });
    }
}
