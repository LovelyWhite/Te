package com.test.te;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

public class AlertListAdapter extends BaseAdapter  {
    private LayoutInflater layoutInflater;
    private Fragment f;
    public AlertListAdapter(Context context, Fragment f)
    {
        layoutInflater  = LayoutInflater.from(context);
        this.f=f;
    }
    @Override
    public int getCount() {
        return Data.alerts.size();
    }

    @Override
    public Object getItem(int position) {
        return Data.alerts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder
    {
        TextView alertID,alertDate,alertTime,alertEle,alertFreq;
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView ==null)
        {
            convertView = layoutInflater.inflate(R.layout.alart,null);
            holder = new ViewHolder();
            holder.alertID = convertView.findViewById(R.id.alertID);
            holder.alertDate = convertView.findViewById(R.id.alertDate);
            holder.alertTime = convertView.findViewById(R.id.alertTime);
            holder.alertEle = convertView.findViewById(R.id.alertEle);
            holder.alertFreq = convertView.findViewById(R.id.alertFreq);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.alertID.setText(Data.alerts.get(position).getAlertID());
        holder.alertDate.setText(Data.alerts.get(position).getAlertDate());
        holder.alertTime.setText(Data.alerts.get(position).getAlertTime());
        holder.alertEle.setText(Data.alerts.get(position).getAlertEle());
        holder.alertFreq.setText(Data.alerts.get(position).getAlertFreq());
        return convertView;
    }

}