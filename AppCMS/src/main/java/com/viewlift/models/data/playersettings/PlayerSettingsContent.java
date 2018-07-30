package com.viewlift.models.data.playersettings;

import com.viewlift.views.adapters.AppCMSDownloadRadioAdapter;

import java.util.ArrayList;

public class PlayerSettingsContent {

    private String settingName;

    private ArrayList<String> listSettingItem;

    private AppCMSDownloadRadioAdapter playerSettingAdapter;

    public PlayerSettingsContent(String name, AppCMSDownloadRadioAdapter adapter){
        settingName = name;
        playerSettingAdapter =adapter;
    }public PlayerSettingsContent(String name, ArrayList<String> list){
        settingName = name;
        listSettingItem =list;
    }

    public ArrayList<String> getListSettingItem() {
        return listSettingItem;
    }

    public void setListSettingItem(ArrayList<String> listSettingItem) {
        this.listSettingItem = listSettingItem;
    }

    public String getSettingName() {
        return settingName;
    }

    public void setSettingName(String settingName) {
        this.settingName = settingName;
    }

    public AppCMSDownloadRadioAdapter getPlayerSettingAdapter() {
        return playerSettingAdapter;
    }

    public void setPlayerSettingAdapter(AppCMSDownloadRadioAdapter playerSettingAdapter) {
        this.playerSettingAdapter = playerSettingAdapter;
    }
}
