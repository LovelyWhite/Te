package com.test.te;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.test.te.model.Device;

import java.io.IOException;
import java.net.Socket;

public class RealTimeData extends Fragment {

    InfoUtils infoUtils= new InfoUtils();
    String deviceId = Data.devices.get(Data.cDevicePosition).getDeviceID();
    String devicePw = Data.devices.get(Data.cDevicePosition).getDevicePW();
    String c,d,e;
    boolean stop = false;
    Handler handler;
    TextView realFreqStatus,realOutFreq,realOutElec,realVolt;
    Thread v;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       // socket = Data.devices.get(Data.cDevicePosition).getSocket();
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Context context = getContext();
                if(context!=null)
                {
                    switch (msg.what) {
                        case -1:
                            Toast.makeText(context, "连接出现异常,请重新登录", Toast.LENGTH_LONG).show();
                        case 1:
                        {
                            double tempc = sax(c);
                            double tempd = sax(d);
                            double tempe = sax(e);
                            if(tempc-0<=0.000001)
                            {
                                realFreqStatus.setText("停止");
                            }
                            else
                            {
                                realFreqStatus.setText("运行中");
                            }
                            realOutFreq.setText(String.valueOf(tempc/100));
                            realOutElec.setText(String.valueOf(tempd));
                            realVolt.setText(String.valueOf(tempe));
                        }
                    }
                }
                return false;
            }
        });
        View view = inflater.inflate(R.layout.fragment_real_time_data, container, false);
        realFreqStatus = view.findViewById(R.id.realFreqStatus);
        realOutFreq = view.findViewById(R.id.realOutFreq);
        realOutElec = view.findViewById(R.id.realOutElec);
        realVolt = view.findViewById(R.id.realVolt);
        return view;
    }

    double sax(String d)
    {
        String a = d.split("&")[1].substring(4, 8);
        return (double) Integer.parseInt(a, 16);
    }

   public void startFlush()
    {
        stop = false;
        v = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        //输出频率
                        c = infoUtils.sendData(">" + deviceId + "&" + "03" + "10010001#", Data.devices.get(Data.cDevicePosition).getSocket(),null);
                        //输出电流
                        d = infoUtils.sendData(">" + deviceId + "&" + "03" + "10030001#", Data.devices.get(Data.cDevicePosition).getSocket(),null);
                        //输出电压
                        e = infoUtils.sendData(">" + deviceId + "&" + "03" + "100A0001#", Data.devices.get(Data.cDevicePosition).getSocket(),null);
                        if(!(c==null||d==null||e==null))
                        {
                            Message m = new Message();
                            m.what = 1;
                            handler.sendMessage(m);
                        }
                        Thread.sleep(2000);
                        if (stop) {
                            break;
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        Message m  = new Message();
                        m.what = -1;
                        handler.sendMessage(m);
                        Data.mainActivity.finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
   //     v.start();
    }

   public void stopFlush()
    {
        if(v!=null&&v.isAlive())
        {
            stop=true;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stop = true;
    }
}
