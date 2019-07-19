package com.test.te;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class ValueListAdapter extends BaseAdapter {
    InfoUtils infoUtils;
    private LayoutInflater layoutInflater;
    private Fragment f;
    Handler handler;

    public ValueListAdapter(final Context context, Fragment f) {
        layoutInflater = LayoutInflater.from(context);
        this.f = f;
        infoUtils = new InfoUtils();
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case -1:
                        Toast.makeText(context, "请输入值", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        notifyDataSetChanged();
                        Toast.makeText(context, "成功", Toast.LENGTH_LONG).show();
                        break;
                    case -2:
                        Toast.makeText(context, "设备不在线", Toast.LENGTH_LONG).show();break;
                    case -3:
                        Toast.makeText(context, "失败", Toast.LENGTH_LONG).show();
                    case -4:
                        Toast.makeText(context, "无此参数", Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });
    }

    @Override
    public int getCount() {
        if( Data.dataLists.size()==0)
        {
            return 0;
        }
        else
        {
            return Data.dataLists.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return Data.dataLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder {

        TextView paraName;
        EditText cValue;
        Button option;
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.c_value, null);
            holder = new ViewHolder();
            holder.paraName = convertView.findViewById(R.id.paraName);
            holder.cValue = convertView.findViewById(R.id.cValue);
            holder.option = convertView.findViewById(R.id.rsvOption);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.paraName.setText(Data.allpCode.get(position).getpCode()+Data.allpCode.get(position).getName());
        holder.cValue.setText(Data.dataLists.get(position).getcValue());
        final ViewHolder finalHolder = holder;
        holder.option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //每次点击需要重新inflate
                View rsv = f.getLayoutInflater().inflate(R.layout.rsv_option, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(f.getContext())
                        .setTitle("选项")
                        .setView(rsv)
                        .create();
                alertDialog.show();
                final Button readValue, writeValue, readDoubleValue,delete;
                readValue = rsv.findViewById(R.id.readValue);
                writeValue = rsv.findViewById(R.id.writeValue);
                readDoubleValue = rsv.findViewById(R.id.readDoubleValue);
                delete = rsv.findViewById(R.id.delete);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Data.showed=Data.showed.replace( Data.dataLists.get(position).getpCode(),"");
                        Data.mainActivity.commit();
                        Data.allpCode.add(Data.dataLists.get(position));
                        Data.dataLists.remove(position);
                        notifyDataSetChanged();
                        alertDialog.dismiss();
                    }
                });
                if (position == Data.dataLists.size()-1)
                {
                    readDoubleValue.setEnabled(false);
                }
                else
                {
                    readDoubleValue.setEnabled(true);
                }
                readValue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String data = ">" + Data.devices.get(Data.cDevicePosition).getDeviceID() + "&" + "03" + Data.dataLists.get(position).getAddress() + "0001#";
                                    if(data.contains("null"))
                                    {
                                        Message m = new Message();
                                        m.what = -4;
                                        handler.sendMessage(m);
                                    }
                                   else
                                    {
                                        String result = infoUtils.sendData(data
                                                , Data.devices.get(Data.cDevicePosition).getSocket()
                                                ,Data.devices.get(Data.cDevicePosition).getPosition());
                                        result = result==null?"":result;
                                        if (result.contains("Drive No online")) {
                                            Message m = new Message();
                                            m.what = -2;//设备不在线
                                            handler.sendMessage(m);
                                        } else if(!result.equals("")) {
                                            String a = result.split("&")[1].substring(4, 8);
                                            String b =Data.dataLists.get(position).getMinUnit();
                                            b= b==null?"1":b;
                                            double v = Integer.parseInt(a, 16) * Double.parseDouble(b);
                                            if(!b.contains("."))
                                            {
                                                Data.dataLists.get(position).setcValue("" + (int)v);
                                            }
                                            else
                                            {
                                                Data.dataLists.get(position).setcValue("" + v);
                                            }
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
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
                writeValue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        if (finalHolder.cValue.getText().toString().length() != 0) {
                            alertDialog.dismiss();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Data.dataLists.get(position).setcValue(finalHolder.cValue.getText().toString());
                                        String b =Data.dataLists.get(position).getMinUnit();
                                        b= b==null?"1":b;
                                        double v = Double.parseDouble(finalHolder.cValue.getText().toString())/Double.parseDouble(b);
                                        String value = Integer.toHexString((int) v);
                                        if (value.length() == 1) {
                                            value = "000" + value;
                                        } else if (value.length() == 2) {
                                            value = "00" + value;
                                        } else if (value.length() == 3) {
                                            value = "0" + value;
                                        }
                                        String data = ">" + Data.devices.get(Data.cDevicePosition).getDeviceID() + "&" + "06" + Data.dataLists.get(position).getAddress() + value + "#";
                                        if(data.contains("null"))
                                        {
                                            Message m = new Message();
                                            m.what = -4;
                                            handler.sendMessage(m);
                                        }
                                        else
                                        {
                                            String result = infoUtils.sendData(data
                                                    , Data.devices.get(Data.cDevicePosition).getSocket()
                                                    , Data.devices.get(Data.cDevicePosition).getPosition());

                                            if(result==null)
                                            {
                                                Message m = new Message();
                                                m.what = -3;
                                                handler.sendMessage(m);
                                            }
                                            else if(result.contains("Drive No online"))
                                            {
                                                finalHolder.cValue.setText("");
                                                Message m = new Message();
                                                m.what = -2;
                                                handler.sendMessage(m);
                                            }
                                            else
                                            {
                                                Message m = new Message();
                                                m.what = 1;
                                                handler.sendMessage(m);
                                            }
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        } else {
                            Message m = new Message();
                            m.what = -1;
                            handler.sendMessage(m);
                        }
                    }
                });
                readDoubleValue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            }

        });

        return convertView;
    }
}