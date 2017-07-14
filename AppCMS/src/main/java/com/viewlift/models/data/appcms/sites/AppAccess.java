package com.viewlift.models.data.appcms.sites;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class AppAccess {

    @SerializedName("appSecretKey")
    @Expose
    String appSecretKey;

    @SerializedName("appId")
    @Expose
    String appId;

    public String getAppSecretKey() {
        return appSecretKey;
    }

    public void setAppSecretKey(String appSecretKey) {
        this.appSecretKey = appSecretKey;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
