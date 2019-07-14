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

import java.io.IOException;


public class RemoteControl extends Fragment {

    DashBoard freq;
    DashBoard ampere;
    Button start,stop,restore;
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
       restore = view.findViewById(R.id.restore);
       handler = new Handler(new Handler.Callback() {
           @Override
           public boolean handleMessage(Message msg) {
               switch (msg.what)
               {

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
                            String result = infoUtils.sendData(">" + Data.devices.get(Data.cDevicePosition).getDeviceID() + "&" + "06" +  "#"
                                    , Data.devices.get(Data.cDevicePosition).getSocket()
                                    , Data.devices.get(Data.cDevicePosition).getPosition());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
