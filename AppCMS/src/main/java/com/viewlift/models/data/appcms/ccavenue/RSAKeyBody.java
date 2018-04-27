package com.viewlift.models.data.appcms.ccavenue;

import com.google.gson.annotations.SerializedName;

public class RSAKeyBody {
    @SerializedName("site")
    String site;
    @SerializedName("userId")
    String userId;
    @SerializedName("device")
    String device;
    @SerializedName("planId")
    String planId;

    public void setSite(String site) {
        this.site = site;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }
}
