package com.viewlift.tv.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.page.ModuleList;

public class BrowseCompnentModule{
    @SerializedName("position")
    public int position;
    @SerializedName("moduleData")
    public Module moduleData;
    @SerializedName("moduleUI")
    public ModuleList moduleUI;

}