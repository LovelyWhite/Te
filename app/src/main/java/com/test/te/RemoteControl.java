package com.test.te;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class RemoteControl extends Fragment {

    DashBoard freq;
    DashBoard ampere;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_remote_control, container, false);
       freq = view.findViewById(R.id.freq);
       ampere = view.findViewById(R.id.ampere);
       return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        freq.setRealTimeValue(1.2);
        ampere.setRealTimeValue(11);
    }
}
