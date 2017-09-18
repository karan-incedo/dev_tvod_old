package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

/**
 * Created by viewlift on 9/18/17.
 */

@UseStag
public class Columns {
    @SerializedName("tablet")
    @Expose
    int tablet;

    @SerializedName("desktop")
    @Expose
    int desktop;

    @SerializedName("mobile")
    @Expose
    int mobile;

    @SerializedName("ott")
    @Expose
    int ott;

    public int getTablet() {
        return tablet;
    }

    public void setTablet(int tablet) {
        this.tablet = tablet;
    }

    public int getDesktop() {
        return desktop;
    }

    public void setDesktop(int desktop) {
        this.desktop = desktop;
    }

    public int getMobile() {
        return mobile;
    }

    public void setMobile(int mobile) {
        this.mobile = mobile;
    }

    public int getOtt() {
        return ott;
    }

    public void setOtt(int ott) {
        this.ott = ott;
    }
}
