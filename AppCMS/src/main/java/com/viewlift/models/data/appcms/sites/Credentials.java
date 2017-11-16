package com.viewlift.models.data.appcms.sites;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

/**
 * Created by viewlift on 11/9/17.
 */

@UseStag
public class Credentials {
    @SerializedName("android")
    @Expose
    String android;

    public String getAndroid() {
        return android;
    }

    public void setAndroid(String android) {
        this.android = android;
    }
}
