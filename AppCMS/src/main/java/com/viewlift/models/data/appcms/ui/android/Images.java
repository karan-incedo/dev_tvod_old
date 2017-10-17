package com.viewlift.models.data.appcms.ui.android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class Images implements Serializable {

    @SerializedName("mobileAppIcon")
    @Expose
    private String mobileAppIcon;

    public String getMobileAppIcon() {
        return mobileAppIcon;
    }

    public void setMobileAppIcon(String mobileAppIcon) {
        this.mobileAppIcon = mobileAppIcon;
    }
}
