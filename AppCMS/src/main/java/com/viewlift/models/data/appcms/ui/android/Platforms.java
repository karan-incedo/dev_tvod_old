package com.viewlift.models.data.appcms.ui.android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

/**
 * Created by viewlift on 11/2/17.
 */

@UseStag
public class Platforms implements Serializable {
    @SerializedName("roku")
    @Expose
    boolean roku;

    @SerializedName("fireTv")
    @Expose
    boolean fireTv;

    @SerializedName("appleTv")
    @Expose
    boolean appleTv;

    @SerializedName("web")
    @Expose
    boolean web;

    @SerializedName("android")
    @Expose
    boolean android;

    @SerializedName("xbox")
    @Expose
    boolean xbox;

    @SerializedName("ios")
    @Expose
    boolean ios;

    public boolean isRoku() {
        return roku;
    }

    public void setRoku(boolean roku) {
        this.roku = roku;
    }

    public boolean isFireTv() {
        return fireTv;
    }

    public void setFireTv(boolean fireTv) {
        this.fireTv = fireTv;
    }

    public boolean isAppleTv() {
        return appleTv;
    }

    public void setAppleTv(boolean appleTv) {
        this.appleTv = appleTv;
    }

    public boolean isWeb() {
        return web;
    }

    public void setWeb(boolean web) {
        this.web = web;
    }

    public boolean isAndroid() {
        return android;
    }

    public void setAndroid(boolean android) {
        this.android = android;
    }

    public boolean isXbox() {
        return xbox;
    }

    public void setXbox(boolean xbox) {
        this.xbox = xbox;
    }

    public boolean isIos() {
        return ios;
    }

    public void setIos(boolean ios) {
        this.ios = ios;
    }
}
