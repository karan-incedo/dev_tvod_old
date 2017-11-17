package com.viewlift.models.data.appcms.sites;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

/**
 * Created by viewlift on 11/9/17.
 */

@UseStag
public class AppStore {
    @SerializedName("appName")
    @Expose
    String appName;

    @SerializedName("itunesUserName")
    @Expose
    String itunesUserName;

    @SerializedName("itunesUserPassword")
    @Expose
    String itunesUserPassword;

    @SerializedName("shortAppName")
    @Expose
    String shortAppName;

    @SerializedName("bundleId")
    @Expose
    String bundleId;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getItunesUserName() {
        return itunesUserName;
    }

    public void setItunesUserName(String itunesUserName) {
        this.itunesUserName = itunesUserName;
    }

    public String getItunesUserPassword() {
        return itunesUserPassword;
    }

    public void setItunesUserPassword(String itunesUserPassword) {
        this.itunesUserPassword = itunesUserPassword;
    }

    public String getShortAppName() {
        return shortAppName;
    }

    public void setShortAppName(String shortAppName) {
        this.shortAppName = shortAppName;
    }

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }
}
