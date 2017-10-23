package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

/**
 * Created by viewlift on 10/4/17.
 */

@UseStag
public class AppVersions implements Serializable {
    @SerializedName("ios")
    @Expose
    AppVersion iosAppVersion;

    @SerializedName("android")
    @Expose
    AppVersion androidAppVersion;

    public AppVersion getIosAppVersion() {
        return iosAppVersion;
    }

    public void setIosAppVersion(AppVersion iosAppVersion) {
        this.iosAppVersion = iosAppVersion;
    }

    public AppVersion getAndroidAppVersion() {
        return androidAppVersion;
    }

    public void setAndroidAppVersion(AppVersion androidAppVersion) {
        this.androidAppVersion = androidAppVersion;
    }
}
