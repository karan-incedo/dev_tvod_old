package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class rent implements Serializable {

    @SerializedName("sd")
    @Expose
    String sd;

    @SerializedName("hd")
    @Expose
    String hd;

    @SerializedName("uhd")
    @Expose
    String uhd;

    @SerializedName("rentalPeriod")
    @Expose
    String rentalPeriod;

    public String getSd() {
        return sd;
    }

    public void setSd(String sd) {
        this.sd = sd;
    }

    public String getHd() {
        return hd;
    }

    public void setHd(String hd) {
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
