package com.test.te;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DeviceListAdapter extends BaseAdapter  {
    private LayoutInflater layoutInflater;
    public DeviceListAdapter(Context context)
    {
        layoutInflater  = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return Data.devices.size();
    }

    @Override
    public Object getItem(int position) {
        return Data.devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder
    {
        TextView deviceID;
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView ==null)
        {
            convertView = layoutInflater.inflate(R.layout.device,null);
            holder = new ViewHolder();
            holder.deviceID = convertView.findViewById(R.id.deviceID);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.deviceID.setText(Data.devices.get(position).getDeviceID());
        return convertView;
    }

}