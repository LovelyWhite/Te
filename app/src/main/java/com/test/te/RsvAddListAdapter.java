package com.test.te;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.test.te.model.CValue;

public class RsvAddListAdapter extends BaseAdapter  {
    private LayoutInflater layoutInflater;
    Activity fContext;

    public RsvAddListAdapter(Activity context)
    {
        fContext = context;
        layoutInflater  = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return Data.allpCode.size();
    }

    @Override
    public Object getItem(int position) {
        return Data.allpCode.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder
    {
        TextView pCode;
        Button buttonOK;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView ==null)
        {
            convertView = layoutInflater.inflate(R.layout.rsv_add_item,null);
            holder = new ViewHolder();
            holder.pCode = convertView.findViewById(R.id.rsvAddName);
            holder.buttonOK = convertView.findViewById(R.id.rsvAdd_OK);
            final ViewHolder finalHolder = holder;
            holder.buttonOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p =(int) finalHolder.buttonOK.getTag();
                    Data.showed+=Data.allpCode.get(p).getpCode();
                    ((RsvAddActivity)fContext).commit();
                    System.out.println(Data.allpCode.get(p).getpCode().substring(0, 2));
                    Data.dataLists.add(Data.allpCode.get(p));
                    fContext.setResult(1);
                    System.out.println(p);
                    Data.allpCode.remove(p);
                    notifyDataSetChanged();
                }
            });
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.buttonOK.setTag(position);
        holder.pCode.setText(Data.allpCode.get(position).getName()+Data.allpCode.get(position).getpCode());
        return convertView;
    }

}