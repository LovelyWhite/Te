package com.test.te;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class AlertAddActivity extends AppCompatActivity {
    private ListView alertAddList;
    public AlertAddListAdapter alertAddListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsv_add);
        alertAddList = findViewById(R.id.rsvAddList);
        alertAddListAdapter = new AlertAddListAdapter(this);
        alertAddList.setAdapter(alertAddListAdapter);
    }
    public void commit() {
        SharedPreferences userSettings = getSharedPreferences("pCodeShowed", 0);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putString("a"+Data.devices.get(Data.cDevicePosition).getDeviceID(), Data.aShowed);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(2);
        commit();
    }
}

