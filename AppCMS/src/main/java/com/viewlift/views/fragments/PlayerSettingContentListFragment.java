package com.viewlift.views.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewlift.R;
import com.viewlift.views.adapters.AppCMSDownloadRadioAdapter;

@SuppressLint("ValidFragment")
public class PlayerSettingContentListFragment extends Fragment{

    RecyclerView settingList;
    AppCMSDownloadRadioAdapter listViewAdapter;


    @SuppressLint("ValidFragment")
    public PlayerSettingContentListFragment(AppCMSDownloadRadioAdapter adapter){
        listViewAdapter = adapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_player_setting_items, container, false);

        settingList = rootView.findViewById(R.id.player_setting_content_list);

        if(listViewAdapter != null) {
            settingList.setAdapter(listViewAdapter);
        }
        settingList.setBackgroundColor(Color.TRANSPARENT/*appCMSPresenter.getGeneralBackgroundColor()*/);
        settingList.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL,
                false));

        return rootView;
    }
}
