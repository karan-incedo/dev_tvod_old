package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DfpAds {

    @SerializedName("deviceType")
    @Expose
    String deviceType;

    @SerializedName("dfpAdTag")
    @Expose
    String dfpAdTag;


    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDfpAdTag() {
        return dfpAdTag;
    }

    public void setDfpAdTag(String dfpAdTag) {
        this.dfpAdTag = dfpAdTag;
    }
}
