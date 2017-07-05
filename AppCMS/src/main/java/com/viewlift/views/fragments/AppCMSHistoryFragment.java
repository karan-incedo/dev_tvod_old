package com.viewlift.views.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import snagfilms.com.air.appcms.R;

public class AppCMSHistoryFragment extends Fragment {


    public AppCMSHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(inflater.getContext()).inflate(R.layout.fragment_history,
                container, false);

        //

        return view;
    }
}
