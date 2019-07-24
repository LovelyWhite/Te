package com.test.te;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class AlertAddListAdapter extends BaseAdapter  {
    private LayoutInflater layoutInflater;
    Activity fContext;

    public AlertAddListAdapter(Activity context)
    {
        fContext = context;
        layoutInflater  = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return Data.allAlerts.size();
    }

    @Override
    public Object getItem(int position) {
        return Data.allAlerts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder
    {
        TextView pCode,pName;
        Button buttonOK;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView ==null)
        {
            convertView = layoutInflater.inflate(R.layout.rsv_add_item,null);
            holder = new ViewHolder();
            holder.pCode = convertView.findViewById(R.id.rsvAddpCode);
            holder.pName = convertView.findViewById(R.id.rsvAddName);
            holder.buttonOK = convertView.findViewById(R.id.rsvAdd_OK);
            final ViewHolder finalHolder = holder;
            holder.buttonOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p =(int) finalHolder.buttonOK.getTag();
                    Data.aShowed+=Data.allAlerts.get(p).getpCode();
                    ((AlertAddActivity)fContext).commit();
                    Data.dataAlerts.add(Data.allAlerts.get(p));
//                    Data.dataLists.forEach((e)->
//                    {
//                        System.out.print(e.getpCode());
//                    });
                    fContext.setResult(2);
                    System.out.println(p);
                    Data.allAlerts.remove(p);
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
        holder.pCode.setText(Data.allAlerts.get(position).getpCode());
        holder.pName.setText(Data.allAlerts.get(position).getName());
        return convertView;
    }

}