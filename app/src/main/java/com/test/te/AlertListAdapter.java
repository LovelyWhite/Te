package com.test.te;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
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
        TextView paraName,cValue;
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView ==null)
        {
            convertView = layoutInflater.inflate(R.layout.alart,null);
            holder = new ViewHolder();
            holder.paraName = convertView.findViewById(R.id.paraName);
            holder.cValue = convertView.findViewById(R.id.cValue);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.paraName.setText(Data.alerts.get(position).getpCode()+Data.alerts.get(position).getName());
        holder.cValue.setText(Data.alerts.get(position).getcValue());
        return convertView;
    }

}