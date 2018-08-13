package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rent {

    @SerializedName("sd")
    @Expose
    private float sd;
    @SerializedName("hd")
    @Expose
    private float hd;
    @SerializedName("uhd")
    @Expose
    private float uhd;
    @SerializedName("rentalPeriod")
    @Expose
    private int rentalPeriod;
    @SerializedName("startingPeriod")
    @Expose
    private int startingPeriod;

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

    public float getUhd() {
        return uhd;
    }

    public void setUhd(float uhd) {
        this.uhd = uhd;
    }

    public int getRentalPeriod() {
        return rentalPeriod;
    }

    public void setRentalPeriod(int rentalPeriod) {
        this.rentalPeriod = rentalPeriod;
    }

    public int getStartingPeriod() {
        return startingPeriod;
    }

    public void setStartingPeriod(int startingPeriod) {
        this.startingPeriod = startingPeriod;
    }
}