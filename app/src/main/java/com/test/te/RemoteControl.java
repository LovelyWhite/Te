package com.test.te;


import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


public class RemoteControl extends Fragment {

    Button start,stop,setFreq;
    EditText freqNum;
    Handler handler;
    TextView currentState;
    InfoUtils infoUtils = new InfoUtils();
    Button readFreq,getState;
    ImageView stateImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_remote_control, container, false);
       start = view.findViewById(R.id.start);
       stop = view.findViewById(R.id.stop);
       setFreq = view.findViewById(R.id.set_freq);
       freqNum = view.findViewById(R.id.freq_num);
       readFreq = view.findViewById(R.id.read_freq);
       currentState = view.findViewById(R.id.currentState);
       getState = view.findViewById(R.id.getState);
       stateImage=view.findViewById(R.id.stateImage);
       handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Context context = getContext();
                if(context!=null)
                {
                    switch (msg.what) {
                        case -1:
                            Toast.makeText(context, "请输入值", Toast.LENGTH_LONG).show();
                            break;
                        case 1:
                            Toast.makeText(context, "成功", Toast.LENGTH_LONG).show();
                            break;
                        case -2:
                            Toast.makeText(context, "设备不在线", Toast.LENGTH_LONG).show();
                            break;
                        case -3:
                            Toast.makeText(context, "失败", Toast.LENGTH_LONG).show();
                            break;
                        case 8:
                            Toast.makeText(context, "成功", Toast.LENGTH_LONG).show();
                            freqNum.setText(Data.ctrl.getFreq());
                            readFreq.setEnabled(true);
                            break;
                        case 9:
                            stateImage.setImageResource(R.drawable.open);
                            currentState.setText("启动");
                            break;
                        case 10:
                            stateImage.setImageResource(R.drawable.close);
                            currentState.setText("停止");
                            break;
                        case 11:
                            getState.setEnabled(true);
                            break;
                        case 12:
                            start.setEnabled(true);
                            break;
                        case 13:
                            stop.setEnabled(true);
                            break;
                        case 14:
                            setFreq.setEnabled(true);
                            break;
                        case 15:
                            readFreq.setEnabled(true);
                            break;
                    }
                }

                return false;
            }
        });
       return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                                m = new Message();
                                m.what = 9;
                                handler.sendMessage(m);
                            }
                            else
                            {
                                Message m = new Message();
                                m.what = -3;
                                handler.sendMessage(m);
                            }
                            //释放按钮
                            Message m = new Message();
                            m.what = 12;
                            handler.sendMessage(m);
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
                                m  = new Message();
                                m.what = 10;
                                handler.sendMessage(m);
                            }
                            else
                            {
                                Message m = new Message();
                                m.what = -3;
                                handler.sendMessage(m);
                            }

                            //释放按钮
                            Message m = new Message();
                            m.what = 13;
                            handler.sendMessage(m);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
        });
        setFreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFreq.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(freqNum.getText().equals(""))
                        {
                            Message m = new Message();
                            m.what = -1;
                            handler.sendMessage(m);
                        }
                        else
                        {
                            try {
                                String num =  freqNum.getText().toString();
                                double n = Double.parseDouble(num);
                                n*=100;
                                num =  Integer.toHexString((int)n);
                                if (num.length() == 1) {
                                    num = "000" + num;
                                } else if (num.length() == 2) {
                                    num = "00" + num;
                                } else if (num.length() == 3) {
                                    num = "0" + num;
                                }
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

                                //释放按钮
                                Message m = new Message();
                                m.what = 14;
                                handler.sendMessage(m);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
                setFreq.setEnabled(true);
            }
        });
        readFreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readFreq.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String data = ">" + Data.devices.get(Data.cDevicePosition).getDeviceID() + "&" + "03" + Data.ctrl.getAd_freq() + "0001#";
                        if (data.contains("null")) {
                        } else {
                            String result = null;
                            try {
                                result = infoUtils.sendData(data
                                        , Data.devices.get(Data.cDevicePosition).getSocket()
                                        , Data.devices.get(Data.cDevicePosition).getPosition());
                                result = result == null ? "" : result;
                                if (result.contains("Drive No online")) {
                                    Message m = new Message();
                                    m.what = -2;
                                    handler.sendMessage(m);

                                } else if (!result.equals("")&&result.contains("&")) {
                                    String a = result.split("&")[1].substring(4, 8);
                                    String b = "0.01";
                                    b = b == null ? "1" : b;
                                    double v = Integer.parseInt(a, 16) * Double.parseDouble(b);
                                    if (!b.contains(".")) {
                                        Data.ctrl.setFreq("" + (int) v);
                                    } else {
                                        Data.ctrl.setFreq("" + v);
                                    }
                                    Message m = new Message();
                                    m.what = 8;
                                    handler.sendMessage(m);
                                }
                                //释放按钮
                                Message m = new Message();
                                m.what = 15;
                                handler.sendMessage(m);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }).start();
            }
        });
        getState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getState.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String data = ">" + Data.devices.get(Data.cDevicePosition).getDeviceID() + "&" + "03" + Data.ctrl.getAd_output() + "0001#";
                        if (data.contains("null")) {
                        } else {
                            String result = null;
                            try {
                                result = infoUtils.sendData(data
                                        , Data.devices.get(Data.cDevicePosition).getSocket()
                                        , Data.devices.get(Data.cDevicePosition).getPosition());
                                result = result == null ? "" : result;
                                if (result.contains("Drive No online")) {
                                    Message m = new Message();
                                    m.what = -2;
                                    handler.sendMessage(m);
                                } else if (!result.equals("")&&result.contains("&")) {
                                    String a = result.split("&")[1].substring(4, 8);
                                    String b = "0.01";
                                    b = b == null ? "1" : b;
                                    double v = Integer.parseInt(a, 16) * Double.parseDouble(b);
                                    if(v>0)
                                    {
                                        Message m = new Message();
                                        m.what = 9;
                                        handler.sendMessage(m);
                                    }
                                    else
                                    {
                                        Message m = new Message();
                                        m.what = 10;
                                        handler.sendMessage(m);
                                    }
//                                    if (!b.contains(".")) {
//                                        Data.ctrl.set("" + (int) v);
//                                    } else {
//                                        Data.ctrl.setAd_output("" + v);
//                                    }
                                }

                                //释放按钮
                                Message m = new Message();
                                m.what = 11;
                                handler.sendMessage(m);
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
