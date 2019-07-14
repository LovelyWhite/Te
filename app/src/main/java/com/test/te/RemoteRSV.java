package com.test.te;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;


public class RemoteRSV extends Fragment {

    private ListView cValueList;
    private Spinner selectData;
    private Button rsvAdd;
    public ValueListAdapter valueListAdapter;
    LayoutInflater inflater;
    View remoteView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_remote_rsv, container, false);
        this.inflater=inflater;
        cValueList = view.findViewById(R.id.cValueList);
        selectData = view.findViewById(R.id.selectData);
        valueListAdapter = new ValueListAdapter(view.getContext(),this);
        cValueList.setAdapter(valueListAdapter);
        rsvAdd = view.findViewById(R.id.rsvAdd);
        remoteView = view;
        return  view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,Data.tableList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectData.setAdapter(adapter);
        selectData.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                Data.nowTable=arg2;
                valueListAdapter.notifyDataSetChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        rsvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(remoteView.getContext(), RsvAddActivity.class);
                startActivityForResult(intent,1);
            }
        });
    }
    public void notifyDataSetChanged()
    {
        valueListAdapter.notifyDataSetChanged();
    }
}
