
package com.viewlift.models.data.appcms.ui.android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Images {

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
