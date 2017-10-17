package com.viewlift.models.data.appcms.ui.android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class Analytics implements Serializable {

    @SerializedName("googleTagManagerId")
    @Expose
    String googleTagManagerId;

    @SerializedName("googleAnalyticsId")
    @Expose
    String googleAnalyticsId;

    @SerializedName("kochavaAppId")
    @Expose
    String kochavaAppId;

    @SerializedName("appflyerDevKey")
    @Expose
    String appflyerDevKey;

    @SerializedName("omnitureAppSDKConfigFile")
    @Expose
    String omnitureAppSDKConfigFile;

    public String getGoogleTagManagerId() {
        return googleTagManagerId;
    }

    public void setGoogleTagManagerId(String googleTagManagerId) {
        this.googleTagManagerId = googleTagManagerId;
    }

    public String getGoogleAnalyticsId() {
        return googleAnalyticsId;
    }

    public void setGoogleAnalyticsId(String googleAnalyticsId) {
        this.googleAnalyticsId = googleAnalyticsId;
    }

    public String getKochavaAppId() {
        return kochavaAppId;
    }

    public void setKochavaAppId(String kochavaAppId) {
        this.kochavaAppId = kochavaAppId;
    }

    public String getAppflyerDevKey() {
        return appflyerDevKey;
    }

    public void setAppflyerDevKey(String appflyerDevKey) {
        this.appflyerDevKey = appflyerDevKey;
    }

    public String getOmnitureAppSDKConfigFile() {
        return omnitureAppSDKConfigFile;
    }

    public void setOmnitureAppSDKConfigFile(String omnitureAppSDKConfigFile) {
        this.omnitureAppSDKConfigFile = omnitureAppSDKConfigFile;
    }
}
