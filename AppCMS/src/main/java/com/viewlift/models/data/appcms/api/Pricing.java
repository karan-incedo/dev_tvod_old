package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class Pricing implements Serializable {

    @SerializedName("rent")
    @Expose
    rent rent;

    @SerializedName("purchase")
    @Expose
    rent purchase;


    public com.viewlift.models.data.appcms.api.rent getRent() {
        return rent;
    }

    public void setRent(com.viewlift.models.data.appcms.api.rent rent) {
        this.rent = rent;
    }

    public com.viewlift.models.data.appcms.api.rent getPurchase() {
        return purchase;
    }

    public void setPurchase(com.viewlift.models.data.appcms.api.rent purchase) {
        this.purchase = purchase;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @SerializedName("type")
    @Expose
    String type;


}
