package com.test.te;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class RsvAddActivity extends AppCompatActivity {
    private ListView rsvAddList;
    RsvAddListAdapter rsvAddListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsv_add);
        rsvAddList = findViewById(R.id.rsvAddList);
        rsvAddListAdapter = new RsvAddListAdapter(this);
        rsvAddList.setAdapter(rsvAddListAdapter);
    }
    public void commit() {
        SharedPreferences userSettings = getSharedPreferences("pCodeShowed", 0);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putString(Data.devices.get(Data.cDevicePosition).getDeviceID(), Data.showed);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(1);
        commit();
    }
}

