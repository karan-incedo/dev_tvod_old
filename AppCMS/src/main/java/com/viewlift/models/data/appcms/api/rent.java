package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class    rent implements Serializable {

    @SerializedName("sd")
    @Expose
    float sd;

    @SerializedName("hd")
    @Expose
    float hd;

    @SerializedName("uhd")
    @Expose
    String uhd;

    @SerializedName("rentalPeriod")
    @Expose
    String rentalPeriod;

    public float getSd() {
        return sd;
    }

    public void setSd(float sd) {
        this.sd = sd;
    }

    public float getHd() {
        return hd;
    }

    public void setHd(float hd) {
        this.hd = hd;
    }

    public String getUhd() {
        return uhd;
    }

    public void setUhd(String uhd) {
        this.uhd = uhd;
    }

    public String getRentalPeriod() {
        return rentalPeriod;
    }

    public void setRentalPeriod(String rentalPeriod) {
        this.rentalPeriod = rentalPeriod;
    }

    public String getStartingPeriod() {
        return startingPeriod;
    }

    public void setStartingPeriod(String startingPeriod) {
        this.startingPeriod = startingPeriod;
    }

    @SerializedName("startingPeriod")
    @Expose
    String startingPeriod;


}
