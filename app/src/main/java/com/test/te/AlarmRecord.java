package com.test.te;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class AlarmRecord extends Fragment {

    ListView alertList;
    public AlertListAdapter alertListAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_record, container, false);
        alertList = view.findViewById(R.id.alertList);
        alertListAdapter = new AlertListAdapter(view.getContext(),this);
        alertList.setAdapter(alertListAdapter);
        return view;
    }
    public void notifyDataSetChanged()
    {
        alertListAdapter.notifyDataSetChanged();
    }

}
